package com.mes.springsecurityapi.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    private final JWTUtil jwtTokenUtil;

    public JwtAuthenticationConverter(JWTUtil jwtTokenUtil) {
        Assert.notNull(jwtTokenUtil, "jwtTokenUtil cannot be null");
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.param}")
    private String tokenParam;

    @Value("${jwt.prefix}")
    private String bearerPrefix;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) throws BadCredentialsException {
        ServerHttpRequest request = exchange.getRequest();
        try {

            Authentication authentication = null;
            String authToken = null;
            String username = null;

            String bearerRequestHeader = exchange.getRequest().getHeaders().getFirst(tokenHeader);

            log.debug("Auth token: ", authToken + " username: " + username);
            log.debug("BearerRequestHeader: ", bearerRequestHeader);
            log.debug("IF CHECK: ", authToken + " - " + request.getQueryParams()  + "-"  + request.getQueryParams());
            log.debug("IF CHECK: ", authToken == null );
            log.debug("IF CHECK: ", request.getQueryParams() != null );
            log.debug("IF CHECK: ", !request.getQueryParams().isEmpty());

            if (bearerRequestHeader != null && bearerRequestHeader.startsWith(bearerPrefix + " ")) {
                authToken = bearerRequestHeader.substring(7);
            }

            if (authToken == null && request.getQueryParams() != null && !request.getQueryParams().isEmpty()) {
                String authTokenParam = request.getQueryParams().getFirst(tokenParam);

                if (authTokenParam != null) authToken = authTokenParam;
            }

            if (authToken != null) {
                try {
                    username = jwtTokenUtil.getUsernameFromToken(authToken);
                } catch (IllegalArgumentException e) {
                    log.error("an error occured during getting username from token", e);
                } catch (Exception e) {
                    log.warn("the token is expired and not valid anymore", e);
                }
            } else {
                log.warn("couldn't find bearer string, will ignore the header");
            }

            log.debug("checking authentication for user " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                return Mono.just(new JwtPreAuthenticationToken(authToken, bearerRequestHeader, username));
            }

            return Mono.just(authentication);
        } catch (Exception e) {
            log.error("Error: " + e);
            throw new BadCredentialsException("Invalid token...");
        }
    }
}
