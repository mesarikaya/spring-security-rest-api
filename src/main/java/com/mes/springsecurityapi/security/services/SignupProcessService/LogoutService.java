package com.mes.springsecurityapi.security.services.SignupProcessService;

import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.DTO.LogoutForm;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 1/5/2021
 */
public interface LogoutService {
    Mono<HttpResponse> logout(LogoutForm logoutForm, ServerHttpResponse serverHttpResponse);
}
