package com.mes.springsecurityapi.config.mail;

import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mesar on 12/30/2020
 */
@Slf4j
@Configuration
public class VertxMailServerConfig {

    private final int port;
    private final String username;
    private final String password;
    private final Boolean sslRequired;
    private final String host;

    public VertxMailServerConfig(@Value("${vertx.mail.port}") int port,
                                 @Value("${vertx.mail.username}") String username,
                                 @Value("${vertx.mail.password}") String password,
                                 @Value("${vertx.mail.ssl}") Boolean sslRequired,
                                 @Value("${vertx.mail.host}") String host) {
        this.port = port;
        this.username = username;
        this.password = password;
        this.sslRequired = sslRequired;
        this.host = host;
    }


    @Bean
    public MailClient configure(){
        log.debug("Host of vertx: {}, port: {}, sslReq: {}", host, port, sslRequired);
        Vertx vertx = Vertx.vertx();
        MailConfig config = new MailConfig();
        config.setHostname(host);
        config.setPort(port);
        config.setSsl(sslRequired);
        config.setStarttls(StartTLSOptions.REQUIRED);
        config.setUsername(username);
        config.setPassword(password);
        MailClient mailClient = MailClient.create(vertx, config);
        log.debug("Vertx mail client is: {}", mailClient.toString());
        return mailClient;
    }
}
