package com.mes.springsecurityapi.repositories.security;

import com.mes.springsecurityapi.domain.security.DTO.UserRoleAndAuthoritiesDTO;
import com.mes.springsecurityapi.domain.security.SecurityUserLibrary;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.security.services.security.JoinService;
import com.mes.springsecurityapi.security.services.security.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityUserLibraryRepository implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private final UserService userService;
    private final JoinService joinService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {

        Mono<Set<UserRoleAndAuthoritiesDTO>> authoritySetMono = joinService.findByUsername(username);
        Mono<User> userMono = userService.findByUserName(username);
        log.debug("****CALLING SECURITY DETAILS REPOSITORY FIND BY USERNAME FUNCTION*****");
        return userMono.zipWith(authoritySetMono)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("User not found in In memory User Details Repository method");
                    return Mono.error(new UsernameNotFoundException("User Not Found"));
                }))
                .map(tuple -> {
                    User user = tuple.getT1();
                    Set<UserRoleAndAuthoritiesDTO> authoritySet = tuple.getT2();
                    return new SecurityUserLibrary(user, authoritySet);
                });
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        log.debug("Password upgrade for user with name '{}'", user.getUsername());
        log.debug("Password upgraded from '{}' to '{}'", user.getPassword(), newPassword);
        Mono<Set<UserRoleAndAuthoritiesDTO>> authoritySetMono = joinService.findByUsername(user.getUsername());
        Mono<User> userMono = userService.findByUserName(user.getUsername());
        return userMono.zipWith(authoritySetMono)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("User not found in In memory User Details Repository method");
                    return Mono.error(new UsernameNotFoundException("User Not Found"));
                }))
                .doOnSuccess(u -> {
                    User userFromTuple = u.getT1();
                    userFromTuple.setPassword(newPassword);
                    userFromTuple.setLastModifiedDate(Timestamp.from(Instant.now()));
                })
                .map(u -> {
                    userService.saveOrUpdateUser(u.getT1());
                    return new SecurityUserLibrary(u.getT1(), u.getT2());
                });
    }
}
