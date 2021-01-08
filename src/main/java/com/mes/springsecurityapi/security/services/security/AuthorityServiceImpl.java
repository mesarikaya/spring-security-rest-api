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
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Objects;
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
                .map(User::getId)
                .flatMapMany(roleRepository::findByUserId);
        Flux<Authority> authoritiesFlux = rolesFlux
                .flatMapSequential(roles -> authoritiesRepository.findByAuthoritiesByRoleId(roles.getId()));

        return authoritiesFlux.collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public Mono<Authority> saveOrUpdate(@NotNull Authority authority) {

        if (Objects.isNull(authority)) {
            return Mono.empty();
        }

        if (Objects.isNull(authority.getPermission())) {
            return Mono.empty();
        }

        return authoritiesRepository.findByPermission(authority.getPermission())
                .next()
                .flatMap(authorityInDb -> {
                    log.debug("Authority in db is: {}", authorityInDb);
                    log.debug("Update the role");
                    authority.setId(authorityInDb.getId());
                    log.debug("Authority in repository: {}", authority);
                    return authoritiesRepository.save(authority);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Creating a new Authority.");
                    log.debug("Authority in repository: {}", authority);
                    return this.createAuthority(authority);
                }));
    }

    private Mono<Authority> createAuthority(Authority authority) {

        if (Objects.isNull(authority)) {
            return Mono.empty();
        }

        return authoritiesRepository.save(authority);
    }

    @Override
    public Mono<Authority> upsert(String permission) {
        return authoritiesRepository.upsert(permission);
    }
}
