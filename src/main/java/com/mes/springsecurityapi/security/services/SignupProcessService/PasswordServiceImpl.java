package com.mes.springsecurityapi.security.services.SignupProcessService;

import com.mes.springsecurityapi.domain.security.DTO.AuthorizedPasswordUpdateVerificationForm;
import com.mes.springsecurityapi.domain.security.DTO.ForgotPasswordForm;
import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.DTO.PasswordUpdateVerificationForm;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.security.services.email.EmailService;
import com.mes.springsecurityapi.security.services.security.SecurityUserLibraryService;
import com.mes.springsecurityapi.security.services.security.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by mesar on 12/28/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PasswordServiceImpl implements PasswordService {

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUserLibraryService securityUserLibraryService;

    @Transactional
    @Override
    public Mono<HttpResponse> sendPasswordUpdateRequest(@NotNull ForgotPasswordForm forgotPasswordForm,
                                                        ServerHttpRequest serverHttpRequest) {

        String username = forgotPasswordForm.getUsername();
        String origin = serverHttpRequest.getHeaders().getOrigin();

        Mono<HttpResponse> badRequestMono = createHttpResponse(HttpStatus.BAD_REQUEST,
                HttpResponse.ResponseType.FAILURE,"Password Verification request has failed! Wrong credentials!");

        Mono<HttpResponse> goodRequestMono =createHttpResponse(HttpStatus.ACCEPTED,
                HttpResponse.ResponseType.SUCCESS,"Password Verification request has been successfully sent!");
        log.debug("Send Password Request Token: Starting password update token delivery for username: {}", username);
        return userService.findByUsername(username)
                .flatMap(user -> {
                    log.debug("Send Password Request Token: verifying for user: {}", user.toString());
                    return renewPasswordVerificationDetails(user.getUsername());
                }).flatMap(user -> {
                    log.debug("Send Password Request Token: Sending verification email as per request.");
                    return Mono.just(emailService.sendPasswordVerificationEmail(user, origin));
                }).flatMap(responseEntity -> {
                    log.debug("Send Password Request Token: Password Update Email has been sent and response is: {}", responseEntity);
                    if (responseEntity.getType() == HttpResponse.ResponseType.SUCCESS){
                        return goodRequestMono;
                    }else{
                        return badRequestMono;
                    }
                }).switchIfEmpty(Mono.defer(() -> {
                    log.debug("Error in sending password update request: {}", "Wrong credentials!");
                    return badRequestMono;
                })).onErrorResume(ex -> createHttpResponse(HttpStatus.BAD_REQUEST,
                        HttpResponse.ResponseType.FAILURE, ex.getCause().getLocalizedMessage()));
    }

    private Mono<HttpResponse> createHttpResponse(HttpStatus httpStatus, HttpResponse.ResponseType type, String message){
        return Mono.just(new HttpResponse(httpStatus, type, message));
    }

    private Mono<User> renewPasswordVerificationDetails(String username) {
        String passwordVerificationToken = UUID.randomUUID().toString();
        Timestamp currentTime = Timestamp.from(Instant.now());
        Timestamp passwordVerificationTokenExpiresAt = Timestamp.from(currentTime.toInstant().plusSeconds(180));
        return userService.findByUsername(username)
                .flatMap( user -> {
                    log.debug("Send Password Request Token: Renewing password update verification details for username: {}", user.getUsername());
                    user.setPasswordUpdateToken(passwordVerificationToken);
                    user.setPasswordTokenExpiresAt(passwordVerificationTokenExpiresAt);
                    user.setIsPasswordTokenVerified(false);
                    user.setLastModifiedDate(currentTime);
                    return userService.saveOrUpdateUser(user);
                });
    }

    @Override
    public Mono<HttpResponse> validatePasswordVerificationTokenAndUpdate(@NotNull PasswordUpdateVerificationForm passwordUpdateVerificationForm){

        String username = passwordUpdateVerificationForm.getUsername();
        String password = passwordUpdateVerificationForm.getNewPassword();
        String token = passwordUpdateVerificationForm.getValidationToken();

        Mono<HttpResponse> badRequestMono = createHttpResponse(HttpStatus.BAD_REQUEST,
                HttpResponse.ResponseType.FAILURE,"Password update token validation is rejected due to security reasons!");

        Mono<HttpResponse> goodRequestMono =createHttpResponse(HttpStatus.ACCEPTED,
                HttpResponse.ResponseType.SUCCESS,"Password update token validation is complete.");

        return userService.findByUsername(username)
                .flatMap( user -> {
                    Timestamp currentTime = Timestamp.from(Instant.now());
                    log.debug("Send Password Request Token: verifying for user: {}", user.toString());
                    log.debug("Send Password Request Token: tokens same?: {}", user.getPasswordUpdateToken().equals(token));
                    log.debug("Send Password Request Token: within time: {}", currentTime.compareTo(user.getPasswordTokenExpiresAt())<=0);
                    log.debug("Send Password Request Token: current time: {}", currentTime);
                    log.debug("Send Password Request Token: expiry time: {}", user.getPasswordTokenExpiresAt());
                    if (user.getPasswordUpdateToken().equals(token)
                            && currentTime.compareTo(user.getPasswordTokenExpiresAt())<=0){
                        log.debug("Send Password Request Token: User password is successfully updated.");
                        return securityUserLibraryService.findByUsername(user.getUsername())
                                .flatMap(userDetails -> securityUserLibraryService.updatePassword(userDetails, password))
                                .flatMap(userDetails -> goodRequestMono)
                                .onErrorResume(ex -> {
                                    log.debug("Send Password Request Token: An exception has occurred during password update validation.");
                                    return createHttpResponse(HttpStatus.BAD_REQUEST,
                                            HttpResponse.ResponseType.FAILURE, ex.getMessage());
                                });
                    }else{
                        return badRequestMono;
                    }
                }).onErrorResume(ex -> {
                    log.debug("Send Password Request Token: An exception has occurred during password update validation.");
                    return createHttpResponse(HttpStatus.BAD_REQUEST,
                            HttpResponse.ResponseType.FAILURE, ex.getMessage());
                });
    }

    @Override
    public Mono<HttpResponse> updateAuthorizedUserPassword(@NotNull AuthorizedPasswordUpdateVerificationForm authorizedPasswordUpdateVerificationForm,
                                                           ServerHttpRequest serverHttpRequest) {

        String username = authorizedPasswordUpdateVerificationForm.getUsername();
        String oldPassword = authorizedPasswordUpdateVerificationForm.getOldPassword();
        String newPassword = authorizedPasswordUpdateVerificationForm.getNewPassword();

        Mono<HttpResponse> badRequestMono = createHttpResponse(HttpStatus.BAD_REQUEST,
                HttpResponse.ResponseType.FAILURE,"Password Update request has failed! Wrong credentials!");

        Mono<HttpResponse> goodRequestMono =createHttpResponse(HttpStatus.ACCEPTED,
                HttpResponse.ResponseType.SUCCESS,"Password Update request has been successfully sent!");
        return userService.findByUsername(username)
                .flatMap( user -> {
                    log.debug("Password Update Request: verifying authorized user: {}", user.toString());
                    if (passwordEncoder.matches(oldPassword, user.getPassword()) && user.getIsPasswordTokenVerified()){
                        log.debug("Password Update Request: User password is successfully updated.");
                        return securityUserLibraryService.findByUsername(user.getUsername())
                                .flatMap(userDetails -> securityUserLibraryService.updatePassword(userDetails, newPassword))
                                .flatMap(userDetails -> goodRequestMono)
                                .onErrorResume(ex -> {
                                    log.debug("Send Password Request Token: An exception has occurred during password update validation.");
                                    return createHttpResponse(HttpStatus.BAD_REQUEST,
                                            HttpResponse.ResponseType.FAILURE, ex.getMessage());
                                });
                    }else{
                        return badRequestMono;
                    }
                }).onErrorResume(ex -> {
                    log.debug("Send Password Request Token: An exception has occurred during password update validation.");
                    return createHttpResponse(HttpStatus.BAD_REQUEST,
                            HttpResponse.ResponseType.FAILURE, ex.getMessage());
                });
    }
}
