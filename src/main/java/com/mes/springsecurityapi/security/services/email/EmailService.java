package com.mes.springsecurityapi.security.services.email;

import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.User;

/**
 * Created by mesar on 12/31/2020
 */
public interface EmailService {

    HttpResponse sendEmail(User user, String origin);
}
