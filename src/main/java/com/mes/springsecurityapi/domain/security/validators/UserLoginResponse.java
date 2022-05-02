package com.mes.springsecurityapi.domain.security.validators;

import com.mes.springsecurityapi.domain.security.DTO.AuthRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class UserLoginResponse implements Serializable {

    private static final long serialVersionUID = 846427467721730235L;

    private AuthRequest user;
    private boolean validated;
    private Map<String, String> errorMessages;
}