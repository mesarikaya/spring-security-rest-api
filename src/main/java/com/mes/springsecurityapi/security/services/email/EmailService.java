package com.mes.springsecurityapi.security.services.email;

import com.mes.springsecurityapi.domain.security.User;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/31/2020
 */
public interface EmailService {

    Mono<ResponseEntity<?>> sendEmail(User user, String origin);
}
