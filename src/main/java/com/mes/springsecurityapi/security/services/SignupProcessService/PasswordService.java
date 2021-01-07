package com.mes.springsecurityapi.security.services.SignupProcessService;

import com.mes.springsecurityapi.domain.security.DTO.AuthorizedPasswordUpdateVerificationForm;
import com.mes.springsecurityapi.domain.security.DTO.ForgotPasswordForm;
import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.DTO.PasswordUpdateVerificationForm;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * Created by mesar on 12/28/2020
 */
public interface PasswordService {

    Mono<HttpResponse> sendPasswordUpdateRequest(@NotNull ForgotPasswordForm forgotPasswordForm, ServerHttpRequest serverHttpRequest);
    Mono<HttpResponse> validatePasswordVerificationTokenAndUpdate(@NotNull PasswordUpdateVerificationForm passwordUpdateVerificationForm);
    Mono<HttpResponse> updateAuthorizedUserPassword(@NotNull AuthorizedPasswordUpdateVerificationForm authorizedPasswordUpdateVerificationForm,
                                                    ServerHttpRequest serverHttpRequest);
}
