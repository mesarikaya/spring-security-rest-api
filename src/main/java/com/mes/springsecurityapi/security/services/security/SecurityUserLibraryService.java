package com.mes.springsecurityapi.security.services.security;

import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Mono;

public interface SecurityUserLibraryService {

    Mono<UserDetails> findByUsername(String userName);
    Mono<UserDetails> updatePassword(UserDetails userDetails,  String password);
}
