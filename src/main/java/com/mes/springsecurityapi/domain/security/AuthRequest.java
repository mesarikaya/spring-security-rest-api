package com.mes.springsecurityapi.domain.security;

import com.mes.springsecurityapi.domain.security.validators.ExtendedEmailValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthRequest {

    @NotNull
    @ExtendedEmailValidator
    private String userName;
    @NotNull
    @Min(3) // TODO: Increase after testing
    private String password;
}
