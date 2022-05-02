package com.mes.springsecurityapi;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableR2dbcRepositories
@EnableWebFlux
public class SpringSecurityApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApiApplication.class, args);
    }

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(
                new ResourceDatabasePopulator(
                        new ClassPathResource("postgres-user.sql"),
                        new ClassPathResource("postgres-role.sql"),
                        new ClassPathResource("postgres-authority.sql"),
                        new ClassPathResource("postgres-role-authorities.sql"),
                        new ClassPathResource("postgres-user-roles.sql")
                ));

        return initializer;
    }
}
