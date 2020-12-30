package com.mes.springsecurityapi.config.mail;

import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean
import io.vertx.core.Vertx;

/**
 * Created by mesar on 12/30/2020
 */
@Slf4j
@Configuration
public class VertxMailServerConfig {

    /*
    //@Value("${vertx.mail.port}")
    private final String port;

    //@Value("${vertx.mail.username}")
    private final String username;

    //@Value("${vertx.mail.password}")
    private final String password;

    //@Value("${vertx.mail.ssl}")
    private final Boolean sslRequired;

    //@Value("${vertx.mail.host}")
    private final String host;

     */
    @Bean
    public MailClient configure(){

        Vertx vertx = Vertx.vertx();
        MailConfig config = new MailConfig();
        config.setHostname("smtp.mailtrap.io");
        config.setPort(2525);
        config.setSsl(true);
        config.setStarttls(StartTLSOptions.REQUIRED);
        config.setUsername("b4beedb413dab9");
        config.setPassword("70ab8c3506a1eb");
        MailClient mailClient = MailClient.create(vertx, config);
        log.debug("Vertx mail client is: {}", mailClient.toString());
        return mailClient;
    }
}
