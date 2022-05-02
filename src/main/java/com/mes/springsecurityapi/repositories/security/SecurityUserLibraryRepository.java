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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {

        Mono<Set<UserRoleAndAuthoritiesDTO>> authoritySetMono = joinService.findByUsername(username);
        Mono<User> userMono = userService.findByUsername(username);
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
        Mono<User> userMono = userService.findByUsername(user.getUsername());
        return userMono.zipWith(authoritySetMono)
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("User not found in In memory User Details Repository method");
                    return Mono.error(new UsernameNotFoundException("User Not Found"));
                })).map(tuple -> {
                    log.debug("Updating user password and account modification details");
                    User userFromTuple = tuple.getT1();
                    userFromTuple.setPassword(passwordEncoder.encode(newPassword));
                    userFromTuple.setLastModifiedDate(Timestamp.from(Instant.now()));
                    userFromTuple.setIsPasswordTokenVerified(true);
                    userFromTuple.setPasswordTokenExpiresAt(Timestamp.from(Instant.now()));
                    userService.saveOrUpdateUser(userFromTuple).subscribe();
                    log.debug("Saved username: {} and password: {}", user.getUsername(), newPassword);
                    return new SecurityUserLibrary(userFromTuple, tuple.getT2());
                });
    }
}
