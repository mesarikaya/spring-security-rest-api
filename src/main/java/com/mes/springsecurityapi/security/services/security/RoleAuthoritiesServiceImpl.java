package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.Authority;
import com.mes.springsecurityapi.domain.security.Role;
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
public class RoleAuthoritiesServiceImpl implements RoleAuthoritiesService{

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleAuthoritiesRepository roleAuthoritiesRepository;

    @Transactional
    @Override
    public Mono<Void> upsert(String roleName, String permission) {

        Flux<Authority> authorityFlux = authorityRepository.findByPermission(permission);
        Flux<Role> roleFlux = roleRepository.findByName(roleName);
        return createRoleAuthoritiesData(roleFlux, authorityFlux);
    }

    private Mono<Void> createRoleAuthoritiesData(Flux<Role> roleFlux, Flux<Authority> authorityFlux) {
        roleFlux.flatMap(role -> authorityFlux
                .flatMap(authority ->
                    roleAuthoritiesRepository
                        .upsertByRoleAndPermission(role.getId(), authority.getId())
                        .doOnError(error -> {
                            log.error("The following error happened on create user Role data method!", error);
                        })
                        .doOnSuccess(success -> {
                            log.debug("UPSERT: Role with id: {} and authority with id: {} is saved.", role.getId(),
                                    authority.getId());
                        })
                )).subscribe();

        return Mono.empty();
    }
}
