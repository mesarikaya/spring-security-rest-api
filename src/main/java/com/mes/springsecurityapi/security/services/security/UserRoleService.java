package com.mes.springsecurityapi.security.services.security;

import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/28/2020
 */
public interface UserRoleService {

    Mono<Void> insert(String username, String roleName);
}
