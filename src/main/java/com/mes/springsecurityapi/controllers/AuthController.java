package com.mes.springsecurityapi.controllers;

import com.mes.springsecurityapi.domain.security.Authority;
import com.mes.springsecurityapi.domain.security.DTO.AuthRequest;
import com.mes.springsecurityapi.domain.security.DTO.AuthResponse;
import com.mes.springsecurityapi.domain.security.DTO.UserDTO;
import com.mes.springsecurityapi.domain.security.DTO.UserRoleAndAuthoritiesDTO;
import com.mes.springsecurityapi.domain.security.SecurityUserLibrary;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.security.jwt.JWTUtil;
import com.mes.springsecurityapi.security.services.security.AuthorityService;
import com.mes.springsecurityapi.security.services.security.JoinService;
import com.mes.springsecurityapi.security.services.security.RegistrationService;
import com.mes.springsecurityapi.security.services.security.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Created by mesar on 12/23/2020
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth/", produces = {MediaType.APPLICATION_JSON_VALUE})
@Transactional
@Controller
public class AuthController {

    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JoinService joinService;
    private final AuthorityService authorityService;
    private final RegistrationService registrationService;

    @Value("${cookie.secure}")
    private boolean isCookieSecure = false;

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest ar, ServerHttpResponse serverHttpResponse) {
        log.info("Searching for user: {} and password: {}", ar.getUserName(), ar.getPassword());

        Mono<Set<Authority>> authoritySetMono = authorityService.getUserAuthorities(ar.getUserName());
        Mono<User> userMono = userService.findByUserName(ar.getUserName());
        Mono<Set<UserRoleAndAuthoritiesDTO>> joinMono = joinService.findByUsername(ar.getUserName());
        return userMono.zipWith(joinMono)
                .map(tuple -> {
                    User user = tuple.getT1();
                    Set<UserRoleAndAuthoritiesDTO> userRolesAndAuths = tuple.getT2();
                    log.info("Found user: {} and roles & authorities: {}", user, userRolesAndAuths);
                    if (passwordEncoder.matches(ar.getPassword(), user.getPassword())) {
                        log.debug("Authorized via Controller!");
                        log.debug("Create a new SecurityUserLibrary object.");
                        SecurityUserLibrary securityUserLibrary = new SecurityUserLibrary(user, userRolesAndAuths);
                        String token = jwtUtil.generateToken(securityUserLibrary);

                        ResponseCookie cookie = ResponseCookie.from("System", token)
                                .sameSite("Strict")
                                .path("/")
                                .maxAge(3000)
                                .secure(isCookieSecure)
                                .httpOnly(true)
                                .build();
                        serverHttpResponse.addCookie(cookie);
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(new AuthResponse(token, user.getUsername()));
                    } else {
                        log.info("Returning unauthorized");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        /*return userMono.zipWith(authoritySetMono)
                .map(tuple -> {
                    User user = tuple.getT1();
                    Set<Authority> authoritySet = tuple.getT2();
                    log.info("Found user: {} and authorities: {}", user.getUsername(), authoritySet);
                    if (passwordEncoder.matches(ar.getPassword(), user.getPassword())) {
                        log.debug("Authorized via Controller!");
                        log.debug("Create a new SecurityUserLibrary object.");
                        SecurityUserLibrary securityUserLibrary = new SecurityUserLibrary(user, authoritySet);
                        String token = jwtUtil.generateToken(securityUserLibrary);

                        ResponseCookie cookie = ResponseCookie.from("System", token)
                                .sameSite("Strict")
                                .path("/")
                                .maxAge(3000)
                                .secure(isCookieSecure)
                                .httpOnly(true)
                                .build();
                        serverHttpResponse.addCookie(cookie);
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(new AuthResponse(token, user.getUsername()));
                    } else {
                        log.info("Returning unauthorized");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());*/
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<?>> registerClient(@RequestBody UserDTO userDTO,
                                                ServerHttpRequest serverHttpRequest) {
        String origin = serverHttpRequest.getHeaders().getOrigin();
        return registrationService.registerClient(userDTO, origin);
    }

}