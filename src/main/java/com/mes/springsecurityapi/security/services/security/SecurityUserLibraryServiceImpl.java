package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.repositories.security.SecurityUserLibraryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class SecurityUserLibraryServiceImpl implements SecurityUserLibraryService {

    private final SecurityUserLibraryRepository securityUserLibraryRepository;

    @Override
    public Mono<UserDetails> findByUserName(String userName) {
        return securityUserLibraryRepository.findByUsername(userName);
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return securityUserLibraryRepository.updatePassword(user, newPassword);
    }
}
