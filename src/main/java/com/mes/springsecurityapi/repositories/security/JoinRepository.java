package com.mes.springsecurityapi.repositories.security;

import com.mes.springsecurityapi.domain.security.DTO.UserRoleAndAuthoritiesDTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Created by mesar on 12/23/2020
 */
@Repository
public interface JoinRepository extends ReactiveCrudRepository<UserRoleAndAuthoritiesDTO, Integer>{

    @Query("SELECT users.id user_id, users.username username, users.password user_password, " +
            "roles.id role_id,  roles.name role_name, " +
            "authorities.id auth_id, authorities.permission auth_permission " +
            "FROM users, user_roles, roles, authorities, role_authorities " +
            "WHERE users.id = user_roles.user_id " +
            "and roles.id = user_roles.role_id " +
            "and roles.id = role_authorities.role_id " +
            "and authorities.id = role_authorities.authority_id " +
            "and users.username = $1")
    Flux<UserRoleAndAuthoritiesDTO> findByUsername(String username);
}
