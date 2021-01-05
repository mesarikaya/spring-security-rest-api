package com.mes.springsecurityapi.controllers;

import com.mes.springsecurityapi.domain.security.DTO.*;
import com.mes.springsecurityapi.security.permissions.ClientPermission;
import com.mes.springsecurityapi.security.services.SignupProcessService.LoginService;
import com.mes.springsecurityapi.security.services.SignupProcessService.LogoutService;
import com.mes.springsecurityapi.security.services.SignupProcessService.RegistrationService;
import com.mes.springsecurityapi.security.services.SignupProcessService.VerificationService;
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


    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest ar, ServerHttpResponse serverHttpResponse) {
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

    @PostMapping("/verify")
    public Mono<HttpResponse> sendUserVerification(@RequestBody SendVerificationForm sendVerificationForm, ServerHttpRequest serverHttpRequest) {
        return verificationService.sendVerificationRequest(sendVerificationForm, serverHttpRequest);
    }

    @PostMapping("/verify/validate")
    public Mono<HttpResponse> validateVerificationToken(@RequestBody ValidateVerificationForm validateVerificationForm){
        return verificationService.validateVerificationToken(validateVerificationForm);
    }
}
