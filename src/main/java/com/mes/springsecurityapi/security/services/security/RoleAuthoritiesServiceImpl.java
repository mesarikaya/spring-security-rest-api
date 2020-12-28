package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.*;
import com.mes.springsecurityapi.repositories.security.AuthorityRepository;
import com.mes.springsecurityapi.repositories.security.RoleAuthoritiesRepository;
import com.mes.springsecurityapi.repositories.security.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/28/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoleAuthoritiesServiceImpl implements RoleAuthoritiesService{

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleAuthoritiesRepository roleAuthoritiesRepository;

    @Override
    public Mono<Void> insert(String roleName, String permission) {
        Flux<Authority> authorityFlux = authorityRepository.findByPermission(permission);
        Flux<Role> roleFlux = roleRepository.findByName(roleName);
        return createRoleAuthoritiesData(roleFlux, authorityFlux);
    }

    private Mono<Void> createRoleAuthoritiesData(Flux<Role> roleFlux, Flux<Authority> authorityFlux) {
        roleFlux.flatMap(role -> authorityFlux
                .map(authority -> {
                    RoleAuthorities roleAuthorities = RoleAuthorities.of(role, authority);
                    return roleAuthoritiesRepository.save(roleAuthorities)
                            .doOnError(error -> {
                                log.error("The following error happened on create user Role data method!", error);
                            })
                            .doOnSuccess(success -> {
                                log.debug("Role with id: {} and authority: {} is saved.", success.getRoleId(), success.getAuthorityId());
                            }).subscribe();
                })).subscribe();

        return Mono.empty();
    }
}
