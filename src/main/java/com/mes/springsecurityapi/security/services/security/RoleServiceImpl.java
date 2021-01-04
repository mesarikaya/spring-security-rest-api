package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.Role;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.repositories.security.RoleRepository;
import com.mes.springsecurityapi.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mesar on 12/25/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Mono<Set<Role>> getUserRoles(String username) {
        Mono<User> userMono = userRepository.findByUsername(username);
        Flux<Role> rolesFlux = userMono
                .map(user-> user.getId())
                .flatMapMany(userId -> roleRepository.findByUserId(userId));

        return rolesFlux.collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public Mono<Role> saveOrUpdate(Role role) {
        if (Objects.isNull(role)){
            if (!Objects.isNull(role.getName())){
                return this.createRole(role);
            } else{
                return roleRepository.findByName(role.getName())
                        .next()
                        .flatMap(roleInDb -> {
                            log.debug("Role in db is: {}", roleInDb);
                            log.debug("Update the role");
                            role.setId(roleInDb.getId());
                            log.debug("Role in repository: " + role);
                            return roleRepository.save(role);
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            log.debug("Creating a new Role.");
                            log.debug("Role in repository: {}", role);
                            return this.createRole(role);
                        }));
            }
        }else{
            log.debug("A Null user data is entered. Do not process!");
            return Mono.empty();
        }
    }

    private Mono<Role> createRole(Role role) {

        if (Objects.isNull(role)) {
            return Mono.empty();
        }

        return roleRepository.save(role);
    }

    @Override
    public Mono<Role> upsert(String name) {
        return roleRepository.upsert(name);
    }
}
