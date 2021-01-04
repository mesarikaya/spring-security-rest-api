package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.Authority;
import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.DTO.SendVerificationForm;
import com.mes.springsecurityapi.domain.security.DTO.UserDTO;
import com.mes.springsecurityapi.domain.security.DTO.ValidateVerificationForm;
import com.mes.springsecurityapi.domain.security.Role;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.repositories.security.UserRepository;
import com.mes.springsecurityapi.security.services.email.EmailService;
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
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final RoleService roleService;
    private final UserService userService;
    private final RoleAuthoritiesService roleAuthoritiesService;
    private final UserRoleService userRolesService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Mono<HttpResponse> registerClient(@NotNull UserDTO userDTO, ServerHttpRequest serverHttpRequest) {

        String origin = serverHttpRequest.getHeaders().getOrigin();
        return userRepository.findByUsername(userDTO.getEmail())
                .hasElement()
                .flatMap( hasElement -> {
                    if (hasElement){
                        log.debug("User: {} already exists.");
                        return Mono.just(new HttpResponse(HttpStatus.IM_USED,
                                HttpResponse.ResponseType.FAILURE,
                                "User already exists in our records!"));
                    }else{
                        log.debug("Creating a new User: {}", userDTO);
                        return this.createRegistration(userDTO, "CLIENT", "client.rights")
                                .flatMap(user -> Mono.just(emailService.sendEmail(user, origin)));
                    }
                });
    }

    private Mono<User> createRegistration(UserDTO userDTO, String role, String authority) {

        Authority clientAuthority = Authority.builder().permission(authority).build();
        Role clientRole = Role.builder().name(role).build();

        String address = userDTO.getStreetName().strip() + ", "
                + userDTO.getHouseNumber().strip() + ", "
                + userDTO.getCity().strip() + ", "
                + userDTO.getCountry().strip() + ", "
                + userDTO.getZipcode().strip();


        String verificationToken = UUID.randomUUID().toString();
        Timestamp currentTime = Timestamp.from(Instant.now());
        Timestamp verificationTokenExpiresAt = Timestamp.from(currentTime.toInstant().plusSeconds(180));

        User user = User.builder()
                .username(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .firstName(userDTO.getFirstName())
                .middleName(userDTO.getMiddleName())
                .lastName(userDTO.getLastName())
                .createdDate(Timestamp.from(Instant.now()))
                .verificationToken(verificationToken)
                .verificationExpiresAt(verificationTokenExpiresAt)
                .isVerified(false)
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .address(address)
                .build();

        roleService.saveOrUpdate(clientRole).subscribe();
        authorityService.saveOrUpdate(clientAuthority).subscribe();
        roleAuthoritiesService.upsert(role, authority).subscribe();
        userRolesService.upsert(user.getUsername(), role).subscribe();

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public Mono<HttpResponse> sendVerificationRequest(@NotNull SendVerificationForm sendVerificationForm,
                                                      ServerHttpRequest serverHttpRequest) {
        String username = sendVerificationForm.getUsername();
        String password = sendVerificationForm.getPassword();
        String origin = serverHttpRequest.getHeaders().getOrigin();

        Mono<HttpResponse> badRequestMono = createHttpResponse(HttpStatus.BAD_REQUEST,
                HttpResponse.ResponseType.FAILURE,"No such user exists! Please register!");

        Mono<HttpResponse> goodRequestMono =createHttpResponse(HttpStatus.ACCEPTED,
                HttpResponse.ResponseType.SUCCESS,"Verification request has been successfully sent!");

        return userService.findByUsername(username)
                .flatMap(user -> {
                    log.debug("Send Verification Request: verifying for user: {}", user.toString());
                    if (passwordEncoder.matches(password, user.getPassword())){
                        log.debug("Send Verification Request: Credentials check has passed.");
                        return renewVerificationDetails(user.getUsername());
                    }else{
                        log.debug("Send Verification Request: Credentials check has failed.");
                        User invalidUser = new User();
                        return Mono.just(invalidUser);
                    }
                })
                .flatMap(user -> {
                    log.debug("Returned user after credentials check: {} ", user.toString());
                    if (user.getId() == null) {
                        log.debug("Send Verification Request: Rejecting request.");
                        return badRequestMono;
                    }else {
                        log.debug("Send Verification Request: Sending verification email as per request.");
                        return Mono.just(emailService.sendEmail(user, origin));
                    }
                })
                .flatMap(responseEntity -> {
                    log.debug("Email has been sent and response is: {}", responseEntity);
                    if (responseEntity.getType() == HttpResponse.ResponseType.SUCCESS){
                        return goodRequestMono;
                    }else{
                        return badRequestMono;
                    }
                }).switchIfEmpty(Mono.defer(() -> {
                    log.debug("Error in user verification: {}", "No such user exists!");
                    return badRequestMono;
                })).onErrorResume(ex -> createHttpResponse(HttpStatus.BAD_REQUEST,
                        HttpResponse.ResponseType.FAILURE, ex.getCause().getLocalizedMessage()));
    }

    private Mono<HttpResponse> createHttpResponse(HttpStatus httpStatus, HttpResponse.ResponseType type, String message){
        return Mono.just(new HttpResponse(httpStatus, type, message));
    }

    private Mono<User> renewVerificationDetails(String username) {
        String verificationToken = UUID.randomUUID().toString();
        Timestamp currentTime = Timestamp.from(Instant.now());
        Timestamp verificationTokenExpiresAt = Timestamp.from(currentTime.toInstant().plusSeconds(180));
        return userService.findByUsername(username)
                .flatMap( user -> {
                    log.debug("Send Verification Request: Renewing verification details for user: {}", user);
                    user.setVerificationToken(verificationToken);
                    user.setVerificationExpiresAt(verificationTokenExpiresAt);
                    user.setIsVerified(false);
                    user.setLastModifiedDate(currentTime);
                    return userService.saveOrUpdateUser(user);
                });
    }

    @Override
    public Mono<HttpResponse> validateVerificationToken(ValidateVerificationForm validateVerificationForm){

        String username = validateVerificationForm.getUsername();
        String password = validateVerificationForm.getPassword();
        String verificationToken = validateVerificationForm.getValidationToken();

        Mono<HttpResponse> badRequestMono = createHttpResponse(HttpStatus.BAD_REQUEST,
                HttpResponse.ResponseType.FAILURE,"Account validation is rejected due to security reasons!");

        Mono<HttpResponse> goodRequestMono =createHttpResponse(HttpStatus.ACCEPTED,
                HttpResponse.ResponseType.SUCCESS,"Account validation is complete");

        return userService.findByUsername(username)
                .flatMap( user -> {
                    Timestamp currentTime = Timestamp.from(Instant.now());
                    log.debug("Send Verification Request: verifying for user: {}", user.toString());
                    log.debug("Send Verification Request: tokens same?: {}", user.getVerificationToken().equals(verificationToken));
                    log.debug("Send Verification Request: passwords match?: {}", passwordEncoder.matches(password, user.getPassword()));
                    log.debug("Send Verification Request: within time: {}", currentTime.compareTo(user.getVerificationExpiresAt())<=0);
                    log.debug("Send Verification Request: current time: {}", currentTime);
                    log.debug("Send Verification Request: expiry time: {}", user.getVerificationExpiresAt());
                    if (user.getVerificationToken().equals(verificationToken)
                            && passwordEncoder.matches(password, user.getPassword())
                            && currentTime.compareTo(user.getVerificationExpiresAt())<=0
                    ){
                        user.setIsVerified(true);
                        user.setLastModifiedDate(currentTime);
                        return userService.saveOrUpdateUser(user);
                    }else{
                        User invalidUser = new User();
                        return Mono.just(invalidUser);
                    }
                })
                .flatMap(user -> {
                    if (user.getId() == null || !user.getIsVerified()) {
                        return badRequestMono;
                    }else {
                        return goodRequestMono;
                    }
                })
                .onErrorResume(ex -> {
                    log.debug("An exception has occurred during account verification validation.");
                    return createHttpResponse(HttpStatus.BAD_REQUEST,
                            HttpResponse.ResponseType.FAILURE, ex.getMessage());
                });
    }

}
