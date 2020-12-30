package com.mes.springsecurityapi.repositories.security;

import com.mes.springsecurityapi.domain.security.UserRoles;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/23/2020
 */
@Repository
public interface UserRolesRepository extends ReactiveCrudRepository<UserRoles, Integer>{

    @Query("INSERT INTO public.user_roles (user_id, role_id) " +
            "VALUES ($1, $2) " +
            "ON CONFLICT ON CONSTRAINT user_roles_user_id_role_id_key " +
            "DO NOTHING")
    Mono<UserRoles> upsertByUserAndRole(int userId, int roleId);
}
