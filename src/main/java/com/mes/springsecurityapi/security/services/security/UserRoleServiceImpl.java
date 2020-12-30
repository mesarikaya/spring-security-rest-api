package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.Role;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.repositories.security.RoleRepository;
import com.mes.springsecurityapi.repositories.security.UserRepository;
import com.mes.springsecurityapi.repositories.security.UserRolesRepository;
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
public class UserRoleServiceImpl implements UserRoleService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRolesRepository userRolesRepository;

    @Transactional
    @Override
    public Mono<Void> upsert(String username, String roleName) {
        Mono<User> userMono = userRepository.findByUsername(username);
        Flux<Role> roleFlux = roleRepository.findByName(roleName);
        return createUserRoleData(userMono, roleFlux);
    }

    private Mono<Void> createUserRoleData(Mono<User> userMono, Flux<Role> roleFlux) {
       roleFlux.flatMap(role -> userMono
               .map(user -> userRolesRepository.upsertByUserAndRole(user.getId(), role.getId())
                            .doOnError(error -> {
                                log.error("The following error happened on create user Role data method!", error);
                            })
                            .doOnSuccess(success -> {
                                log.debug("UPSERT: User with id: {} and role with id: {} is saved.",
                                        user.getId(), role.getId());
                            }).subscribe()
               )).subscribe();
       return Mono.empty();
    }

}
