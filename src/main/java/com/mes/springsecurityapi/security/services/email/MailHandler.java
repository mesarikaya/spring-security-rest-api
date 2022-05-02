package com.mes.springsecurityapi.security.services.email;


import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
/**
 * Created by mesar on 12/30/2020
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MailHandler {

    private final MailClient mailClient;

    public Mono<ServerResponse> send(ServerRequest request) {
        return request.formData()
                .log()
                .map(this::formToMessage)
                .map(mailClient::sendMail)
                .flatMap(result -> noContent().build());
    }

    private MailMessage formToMessage(MultiValueMap<String, String> form) {
        return new MailMessage()
                .setFrom(form.getFirst("from"))
                .setTo(form.get("to"))
                .setSubject(form.getFirst("subject"))
                .setText(form.getFirst("text"));
    }

}