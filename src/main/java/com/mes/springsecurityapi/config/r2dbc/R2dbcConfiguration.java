package com.mes.springsecurityapi.config.r2dbc;

import com.mes.springsecurityapi.config.security.DatabaseConfigProperties;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by mesar on 12/25/2020
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

    private final DatabaseConfigProperties databaseConfigProperties;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        //ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:postgresql://localhost:5433/<database>");
        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .host(databaseConfigProperties.getHost())
                .database(databaseConfigProperties.getName())
                .port(databaseConfigProperties.getPort())
                .username(databaseConfigProperties.getUsername())
                .password(databaseConfigProperties.getPassword())
                .build());
    }

    @Bean
    ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }
}
