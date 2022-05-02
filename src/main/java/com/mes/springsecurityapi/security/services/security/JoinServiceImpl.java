package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.DTO.UserRoleAndAuthoritiesDTO;
import com.mes.springsecurityapi.repositories.security.JoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mesar on 12/30/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JoinServiceImpl implements JoinService{

    private final JoinRepository joinRepository;

    @Override
    public Mono<Set<UserRoleAndAuthoritiesDTO>> findByUsername(String username) {
        return joinRepository.findByUsername(username).collect(Collectors.toSet());
    }
}