package com.mes.springsecurityapi.security.services.security;

import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/28/2020
 */
public interface RoleAuthoritiesService {

    Mono<Void> upsert(String roleName, String permission);
}
