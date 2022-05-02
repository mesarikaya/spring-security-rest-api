package com.mes.springsecurityapi.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mesar on 12/30/2020
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "database.postgres")
public class DatabaseConfigProperties {
    private String name;
    private String host;
    private int port;
    private String username;
    private String password;
}