package com.mes.springsecurityapi.domain.security.DTO;

import com.mes.springsecurityapi.domain.security.validators.ExtendedEmailValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthRequest implements Serializable {

    private static final long serialVersionUID = 6416301885126060786L;

    @NotNull
    @ExtendedEmailValidator
    private String username;
    @NotNull
    @Min(3) // TODO: Increase after testing
    private String password;
}
