package com.mes.springsecurityapi.repositories.security;

import com.mes.springsecurityapi.domain.security.RoleAuthorities;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/23/2020
 */
@Repository
public interface RoleAuthoritiesRepository extends ReactiveCrudRepository<RoleAuthorities, Integer>{

    @Query("SELECT * FROM role_authorities WHERE role_authorities.role_id = $1 and role_authorities.authority_id = $2")
    Flux<RoleAuthorities> findByRoleAndPermission(int roleId, int authId);

    @Query("INSERT INTO role_authorities (role_id, authority_id) " +
            "VALUES ($1, $2) " +
            "ON CONFLICT ON CONSTRAINT role_authorities_role_id_authority_id_key " +
            "DO NOTHING")
    Mono<RoleAuthorities> upsertByRoleAndPermission(int roleId, int authId);
}
