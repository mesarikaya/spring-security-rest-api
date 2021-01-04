package com.mes.springsecurityapi.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Created by mesar on 12/23/2020
 */
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsGlobalConfiguration implements WebFluxConfigurer {

    private String allowedOrigin;

    public CorsGlobalConfiguration(@Value("${cors.allowed_origins}") String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    public CorsGlobalConfiguration() {
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET, POST, DELETE, OPTIONS", "PUT")
                .allowedHeaders("X-PINGOTHER","Origin","X-Requested-With","X-HTTP-Method-Override", "Content-Type","Accept","X-Auth-Token")
                .allowCredentials(true)
                .exposedHeaders("Access-Control-Expose-Headers", "Authorization", "Cache-Control",
                        "Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin",
                        "X-Requested-With","X-HTTP-Method-Override", "Content-Type","Accept")
                .maxAge(3600000);
    }
}
