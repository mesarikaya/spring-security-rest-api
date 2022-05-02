package com.mes.springsecurityapi.repositories.security;

import com.mes.springsecurityapi.domain.security.Authority;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/23/2020
 */
public interface AuthorityRepository extends ReactiveCrudRepository<Authority, Integer> {

    @Query("SELECT * FROM authorities WHERE permission = $1")
    Flux<Authority> findByPermission(String permission);

    @Query("SELECT authorities.* FROM authorities, role_authorities"
            + " WHERE authorities.id = role_authorities.authority_id and role_authorities.role_id = $1"
            + " ORDER BY role_authorities.id")
    Flux<Authority> findByAuthoritiesByRoleId(Integer roleId);

    @Query("INSERT INTO authorities (permission) " +
            "VALUES ($1) " +
            "ON CONFLICT ON CONSTRAINT authorities_permission_key " +
            "DO NOTHING")
    Mono<Authority> upsert(String permission);
}
