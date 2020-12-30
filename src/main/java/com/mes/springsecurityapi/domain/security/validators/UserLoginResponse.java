package com.mes.springsecurityapi.domain.security.validators;

import com.mes.springsecurityapi.domain.security.DTO.AuthRequest;
import lombok.Data;

import java.util.Map;

@Data
public class UserLoginResponse {

    private AuthRequest user;
    private boolean validated;
    private Map<String, String> errorMessages;

}