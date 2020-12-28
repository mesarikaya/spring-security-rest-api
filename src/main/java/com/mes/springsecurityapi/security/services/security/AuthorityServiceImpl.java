package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.Authority;
import com.mes.springsecurityapi.domain.security.Role;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.repositories.security.AuthorityRepository;
import com.mes.springsecurityapi.repositories.security.RoleRepository;
import com.mes.springsecurityapi.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mesar on 12/25/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorityServiceImpl implements AuthorityService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authoritiesRepository;

    @Override
    public Mono<Set<Authority>> getUserAuthorities(String username) {
        Mono<User> userMono = userRepository.findByUsername(username);
        Flux<Role> rolesFlux = userMono
                .map(user-> user.getId())
                .flatMapMany(userId -> roleRepository.findByUserId(userId));
        Flux<Authority> authoritiesFlux = rolesFlux
                .flatMapSequential(roles -> authoritiesRepository.findByAuthoritiesByRoleId(roles.getId()));

        return authoritiesFlux.collect(Collectors.toSet());
    }
}
