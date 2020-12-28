package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.User;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/23/2020
 */
public interface UserService {

    Mono<User> saveOrUpdateUser(User user);
    Mono<User> findByUserName(String username);
}