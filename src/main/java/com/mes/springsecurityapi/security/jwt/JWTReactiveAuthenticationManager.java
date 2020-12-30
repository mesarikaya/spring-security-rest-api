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

        String username;
        try {
            username = jwtTokenUtil.getUsernameFromToken(authToken);
            if (jwtTokenUtil.validateToken(authToken)) {
                Claims claims = jwtTokenUtil.getAllClaimsFromToken(authToken);
                //String roleInClaim = claims.get("role", String.class);
                //Role role = Role.builder().name(roleInClaim).build();
                log.info("Authenticating for authorities: {}", claims.get("authorities", List.class));
                List<String> authoritiesMap = claims.get("authorities", List.class);
                Set<Authority> authorities = new HashSet<>();
                authoritiesMap.forEach(( authority ) -> authorities.add(Authority.builder().permission(authority).build()));

                //role.setAuthorities(authorities);

                log.info("HERE IN AUTHENTICATE*****: " + "- Authorities: " + authorities);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities.stream()
                                .map(authority -> new SimpleGrantedAuthority(authority.getPermission())) // TODO: Check if ROLE_ is needed
                                .collect(Collectors.toList())
                );
                log.info("Finalized auth: " + auth);
                return Mono.just(auth);
            }else{
                log.info("Invalid token. Send null");
                 return Mono.empty();
            }
        } catch (Exception ex) {
                log.info("Error in examining token");
                return Mono.empty();
        }
    }
}
