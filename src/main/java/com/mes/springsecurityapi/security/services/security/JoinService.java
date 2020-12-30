package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.DTO.UserRoleAndAuthoritiesDTO;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Created by mesar on 12/30/2020
 */
public interface JoinService {

    Mono<Set<UserRoleAndAuthoritiesDTO>> findByUsername(String username);
}
