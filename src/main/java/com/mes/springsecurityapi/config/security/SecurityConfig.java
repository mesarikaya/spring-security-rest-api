package com.mes.springsecurityapi.config.security;

import com.mes.springsecurityapi.repositories.security.SecurityContextRepository;
import com.mes.springsecurityapi.security.SecurityPasswordEncoderFactories;
import com.mes.springsecurityapi.security.jwt.JWTReactiveAuthenticationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * Created by mesar on 12/22/2020
 */

@RequiredArgsConstructor
@Configuration
@Slf4j
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${cors.allowed_origin}")
    private String allowedOrigin;

    private final JWTReactiveAuthenticationManager jwtReactiveAuthenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    PasswordEncoder passwordEncoder() {
        return SecurityPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
            .exceptionHandling()
            .authenticationEntryPoint((swe, e) -> {
                log.info("Inside access entry handler");
                return Mono.fromRunnable(() -> {
                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                });
            })
            .accessDeniedHandler((swe, e) -> {
                log.info("Inside access denied handler");
                return Mono.fromRunnable(() -> {
                    swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                });
            }).and()
            .cors()
            .and()
            .csrf().disable()
            .authorizeExchange()
            .and()
            .httpBasic().disable()
            .formLogin().disable()
            .authenticationManager(jwtReactiveAuthenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange()
                .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .matchers(EndpointRequest.to("health")).permitAll()
                .matchers(EndpointRequest.to("info")).permitAll()
                .pathMatchers("/").permitAll()
                .pathMatchers("/api/auth/login**").permitAll()
                .pathMatchers("/api/auth/logout**").permitAll()
                .pathMatchers("/api/auth/register").permitAll()
                .pathMatchers("/api/auth/verify**").permitAll()
                .pathMatchers("/api/auth/verify/validate**").permitAll()
                .anyExchange().authenticated()
            .and();

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        log.debug("Allowed origin is: " + allowedOrigin);
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Collections.singletonList(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS", "PUT"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
