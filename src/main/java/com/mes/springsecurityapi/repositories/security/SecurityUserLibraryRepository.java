package com.mes.springsecurityapi.repositories.security;

import com.mes.springsecurityapi.domain.security.Authority;
import com.mes.springsecurityapi.domain.security.SecurityUserLibrary;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.security.services.security.AuthorityService;
import com.mes.springsecurityapi.security.services.security.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityUserLibraryRepository implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private final UserService userService;
    private final AuthorityService authorityService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {

        Mono<Set<Authority>> authoritySetMono = authorityService.getUserAuthorities(username);
        Mono<User> userMono = userService.findByUserName(username);
        log.debug("****CALLING SECURITY DETAILS REPOSITORY FIND BY USERNAME FUNCTION*****");
        return userMono.zipWith(authoritySetMono)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("User not found in In memory User Details Repository method");
                    return Mono.error(new UsernameNotFoundException("User Not Found"));
                }))
                .map(tuple -> {
                    User user = tuple.getT1();
                    Set<Authority> authoritySet = tuple.getT2();
                    return new SecurityUserLibrary(user, authoritySet);
                });
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        log.debug("Password upgrade for user with name '{}'", user.getUsername());
        log.debug("Password upgraded from '{}' to '{}'", user.getPassword(), newPassword);
        Mono<Set<Authority>> authoritySetMono = authorityService.getUserAuthorities(user.getUsername());
        Mono<User> userMono = userService.findByUserName(user.getUsername());
        return userMono.zipWith(authoritySetMono)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("User not found in In memory User Details Repository method");
                    return Mono.error(new UsernameNotFoundException("User Not Found"));
                }))
                .doOnSuccess(u -> u.getT1().setPassword(newPassword))
                .map(u -> {
                    userService.saveOrUpdateUser(u.getT1());
                    return new SecurityUserLibrary(u.getT1(), u.getT2());
                });
    }
}
