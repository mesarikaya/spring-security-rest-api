package com.mes.springsecurityapi.security.jwt;


import com.mes.springsecurityapi.domain.security.Authority;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTUtil jwtTokenUtil;
    
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        log.debug("Inside authenticate function from JWTManager.");
        String username;
        try {
            username = jwtTokenUtil.getUsernameFromToken(authToken);
            if (jwtTokenUtil.validateToken(authToken)) {
                Claims claims = jwtTokenUtil.getAllClaimsFromToken(authToken);
                log.debug("Authenticating for authorities: {}", claims.get("authorities", List.class));
                List<String> authoritiesMap = claims.get("authorities", List.class);
                Set<Authority> authorities = new HashSet<>();
                authoritiesMap.forEach(( authority ) -> authorities.add(Authority.builder().permission(authority).build()));
                log.debug("Authorities set: {}", authorities);

                log.debug("HERE IN AUTHENTICATE*****: " + "- Authorities: " + authorities);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities.stream()
                                .map(authority -> new SimpleGrantedAuthority(authority.getPermission()))
                                .collect(Collectors.toList())
                );
                log.debug("Finalized auth: " + auth);
                return Mono.just(auth);
            }else{
                log.debug("Invalid token. Send null");
                return Mono.empty();
            }
        } catch (Exception ex) {
                log.debug("Error in examining token {}", ex.getMessage());
                return Mono.empty();
        }
    }
}
