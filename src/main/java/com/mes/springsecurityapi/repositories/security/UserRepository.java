package com.mes.springsecurityapi.repositories.security;

import com.mes.springsecurityapi.domain.security.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/23/2020
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Integer>{

    @Query("SELECT * FROM users WHERE username = $1")
    Mono<User> findByUsername(String username);
}
