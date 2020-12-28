package com.mes.springsecurityapi.security.jwt;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class JwtAuthenticationWebFilter extends AuthenticationWebFilter {

    public JwtAuthenticationWebFilter(final ReactiveAuthenticationManager authenticationManager,
                                      final JwtAuthenticationConverter converter,
                                      final UnauthorizedAuthenticationEntryPoint entryPoint) {

        super(authenticationManager);

        Assert.notNull(authenticationManager, "authenticationManager cannot be null");
        Assert.notNull(converter, "converter cannot be null");
        Assert.notNull(entryPoint, "entryPoint cannot be null");

        setServerAuthenticationConverter(converter);
        setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(entryPoint));
        setRequiresAuthenticationMatcher(new JWTHeadersExchangeMatcher());
    }

    private static class JWTHeadersExchangeMatcher implements ServerWebExchangeMatcher {
        @Override
        public Mono<MatchResult> matches(final ServerWebExchange exchange) {
            Mono<ServerHttpRequest> request = Mono.just(exchange).map(ServerWebExchange::getRequest);

            /* Check for header "authorization" or parameter "token" */
            return request.map(ServerHttpRequest::getHeaders)
                    .filter(h -> h.containsKey("authorization"))
                    .flatMap($ -> MatchResult.match())
                    .switchIfEmpty(request.map(ServerHttpRequest::getQueryParams)
                            .filter(h -> h.containsKey("token"))
                            .flatMap($ -> MatchResult.match())
                            .switchIfEmpty(MatchResult.notMatch())
                    );
        }
    }
}