package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.Role;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Created by mesar on 12/25/2020
 */
public interface RoleService {

    Mono<Set<Role>> getUserRoles(String username);

    Mono<Role> saveOrUpdate(Role role);

    Mono<Role> upsert(String name);
}
