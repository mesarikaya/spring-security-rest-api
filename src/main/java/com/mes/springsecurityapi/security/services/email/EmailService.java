package com.mes.springsecurityapi.security.services.email;

import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.User;

import javax.validation.constraints.NotNull;

/**
 * Created by mesar on 12/31/2020
 */
public interface EmailService {

    HttpResponse sendAccountVerificationEmail(@NotNull User user, String origin);
    HttpResponse sendPasswordVerificationEmail(@NotNull User user, String origin);
}
