package com.mes.springsecurityapi.security.services.SignupProcessService;

import com.mes.springsecurityapi.domain.security.DTO.AuthRequest;
import com.mes.springsecurityapi.domain.security.DTO.AuthResponse;
import com.mes.springsecurityapi.domain.security.DTO.UserRoleAndAuthoritiesDTO;
import com.mes.springsecurityapi.domain.security.SecurityUserLibrary;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.security.jwt.JWTUtil;
import com.mes.springsecurityapi.security.services.security.JoinService;
import com.mes.springsecurityapi.security.services.security.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

/**
 * Created by mesar on 12/28/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {

    private final JoinService joinService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Value("${cookie.secure}")
    private boolean isCookieSecure = false;

    @Override
    public Mono<ResponseEntity<?>> login(@NotNull AuthRequest ar, ServerHttpResponse serverHttpResponse){

        log.debug("Searching for username: {} and password: {}", ar.getUsername(), ar.getPassword());

        Mono<User> userMono = userService.findByUsername(ar.getUsername());
        Mono<Set<UserRoleAndAuthoritiesDTO>> joinMono = joinService.findByUsername(ar.getUsername());
        return userMono.zipWith(joinMono)
                .map(tuple -> {

                    User user = tuple.getT1();
                    Set<UserRoleAndAuthoritiesDTO> userRolesAndAuths = tuple.getT2();

                    log.info("Found username: {} and roles & authorities: {}", user.getUsername(), userRolesAndAuths);
                    if (passwordEncoder.matches(ar.getPassword(), user.getPassword()) && user.getIsVerified()) {

                        String token = generateSecurityToken(user, userRolesAndAuths);
                        createLoginSessionCookie(serverHttpResponse, token);

                        user.setLastLogin(Timestamp.from(Instant.now()));
                        userService.saveOrUpdateUser(user).subscribe();

                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(new AuthResponse(token, user.getUsername()));
                    } else {
                        log.info("Password does not match. Unauthorized");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private String generateSecurityToken(User user, Set<UserRoleAndAuthoritiesDTO> userRolesAndAuths) {
        SecurityUserLibrary securityUserLibrary = new SecurityUserLibrary(user, userRolesAndAuths);
        String token = jwtUtil.generateToken(securityUserLibrary);
        return token;
    }

    private void createLoginSessionCookie(ServerHttpResponse serverHttpResponse, String token) {
        ResponseCookie cookie = ResponseCookie.from("System", token)
                .sameSite("Strict")
                .path("/")
                .maxAge(3000)
                .secure(isCookieSecure)
                .httpOnly(true)
                .build();
        serverHttpResponse.addCookie(cookie);
    }
}
