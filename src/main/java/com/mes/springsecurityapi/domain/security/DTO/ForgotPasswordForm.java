package com.mes.springsecurityapi.domain.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by mesar on 1/7/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ForgotPasswordForm implements Serializable {

    private static final long serialVersionUID = -8080846539501523050L;

    @NotNull
    private String username;
}
