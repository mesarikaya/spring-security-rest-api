package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.DTO.UserDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * Created by mesar on 12/28/2020
 */
public interface RegistrationService {

    Mono<ResponseEntity<?>> registerClient(@NotNull UserDTO userDTO);
}
