package com.mes.springsecurityapi.repositories.security;

import com.mes.springsecurityapi.domain.security.Role;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * Created by mesar on 12/23/2020
 */
public interface RoleRepository extends ReactiveCrudRepository<Role, Integer> {

    @Query("SELECT * FROM roles WHERE name = $1")
    Flux<Role> findByName(String name);

    @Query("SELECT roles.* " +
            "FROM roles, user_roles " +
            "WHERE roles.id = user_roles.role_id and user_roles.user_id = $1 " +
            "ORDER BY user_roles.id")
    Flux<Role> findByUserId(Integer userId);
}
