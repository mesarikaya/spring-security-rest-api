package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
/**
 * Created by mesar on 12/23/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public Mono<User> saveOrUpdateUser(User user) {

        if (Objects.isNull(user)) {
            return Mono.empty();
        }

        if (Objects.isNull(user.getUsername())) {
            return Mono.empty();
        }

        return userRepository.findByUsername(user.getUsername())
                .flatMap(userInDb -> {
                    log.debug("user in db is: {}", userInDb);
                    log.debug("Update the user");
                    user.setId(userInDb.getId());
                    log.debug("User in repository: {}", user);
                    return userRepository.save(user);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Creating a new User");
                    log.debug("User in repository: {}", user);
                    return this.createUser(user);
                }));
    }

    private Mono<User> createUser(User user) {

        if (Objects.isNull(user)) {
            return Mono.empty();
        }

        return userRepository.save(user);
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
