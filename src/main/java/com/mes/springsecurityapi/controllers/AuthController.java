package com.mes.springsecurityapi.controllers;

import com.mes.springsecurityapi.domain.security.DTO.*;
import com.mes.springsecurityapi.security.permissions.ClientPermission;
import com.mes.springsecurityapi.security.services.SignupProcessService.*;
import com.mes.springsecurityapi.security.services.security.SecurityUserLibraryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * Created by mesar on 12/23/2020
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth/", produces = {MediaType.APPLICATION_JSON_VALUE})
@RestController
public class AuthController {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final VerificationService verificationService;
    private final PasswordService passwordService;
    private final SecurityUserLibraryService securityUserLibraryService;


    @PostMapping("/login")
    public Mono<ResponseEntity<? extends Serializable>> login(@RequestBody AuthRequest ar, ServerHttpResponse serverHttpResponse) {
        return loginService.login(ar, serverHttpResponse);
    }

    @ClientPermission
    @PostMapping("/logout")
    public Mono<HttpResponse> logout(@RequestBody LogoutForm logoutForm, ServerHttpResponse serverHttpResponse){
        return logoutService.logout(logoutForm, serverHttpResponse);
    }

    @PostMapping("/register")
    public Mono<HttpResponse> registerClient(@RequestBody UserDTO userDTO, ServerHttpRequest serverHttpRequest) {
        return registrationService.registerClient(userDTO, serverHttpRequest);
    }

    @PostMapping("/verify/account")
    public Mono<HttpResponse> sendUserAccountVerification(@RequestBody SendVerificationForm sendVerificationForm, ServerHttpRequest serverHttpRequest) {
        return verificationService.sendVerificationRequest(sendVerificationForm, serverHttpRequest);
    }

    @PostMapping("/verify/account/validate")
    public Mono<HttpResponse> validateAccountVerificationToken(@RequestBody UserVerificationForm userVerificationForm){
        return verificationService.validateVerificationToken(userVerificationForm);
    }

    @PostMapping("/verify/password")
    public Mono<HttpResponse> sendUserPasswordVerification(@RequestBody ForgotPasswordForm forgotPasswordForm, ServerHttpRequest serverHttpRequest) {
        return passwordService.sendPasswordUpdateRequest(forgotPasswordForm, serverHttpRequest);
    }

    @PostMapping("/verify/password/validate")
    public Mono<HttpResponse> validatePasswordUpdateVerificationToken(@RequestBody PasswordUpdateVerificationForm passwordUpdateVerificationForm){
        return passwordService.validatePasswordVerificationTokenAndUpdate(passwordUpdateVerificationForm);
    }

    @ClientPermission
    @PostMapping("/update/password")
    public Mono<HttpResponse> updatePassword(@RequestBody AuthorizedPasswordUpdateVerificationForm authorizedPasswordUpdateVerificationForm,
                                             ServerHttpRequest serverHttpRequest) {
        return passwordService.updateAuthorizedUserPassword(authorizedPasswordUpdateVerificationForm, serverHttpRequest);
    }
}
