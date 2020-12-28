package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.Authority;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Created by mesar on 12/25/2020
 */
public interface AuthorityService {
    Mono<Set<Authority>> getUserAuthorities(String username);
}
