package com.mes.springsecurityapi.security.services.SignupProcessService;

import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.DTO.SendVerificationForm;
import com.mes.springsecurityapi.domain.security.DTO.ValidateVerificationForm;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * Created by mesar on 12/28/2020
 */
public interface VerificationService {

    Mono<HttpResponse> sendVerificationRequest(@NotNull SendVerificationForm sendVerificationForm, ServerHttpRequest serverHttpRequest);
    Mono<HttpResponse> validateVerificationToken(@NotNull ValidateVerificationForm validateVerificationForm);
}
