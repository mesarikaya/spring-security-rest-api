package com.mes.springsecurityapi.security.services.SignupProcessService;

import com.mes.springsecurityapi.domain.security.DTO.AuthRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * Created by mesar on 12/28/2020
 */
public interface LoginService {

    Mono<ResponseEntity<?>>  login(@NotNull AuthRequest ar, ServerHttpResponse serverHttpResponse);
}
