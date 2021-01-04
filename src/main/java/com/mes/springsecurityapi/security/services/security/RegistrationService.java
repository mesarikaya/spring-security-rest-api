package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.DTO.SendVerificationForm;
import com.mes.springsecurityapi.domain.security.DTO.UserDTO;
import com.mes.springsecurityapi.domain.security.DTO.ValidateVerificationForm;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * Created by mesar on 12/28/2020
 */
public interface RegistrationService {

    Mono<HttpResponse> registerClient(@NotNull UserDTO userDTO, ServerHttpRequest serverHttpRequest);
    Mono<HttpResponse> sendVerificationRequest(@NotNull SendVerificationForm sendVerificationForm, ServerHttpRequest serverHttpRequest);
    Mono<HttpResponse> validateVerificationToken(@RequestBody ValidateVerificationForm validateVerificationForm);
}
