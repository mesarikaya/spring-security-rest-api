package com.mes.springsecurityapi.domain.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by mesar on 1/4/2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SendVerificationForm implements Serializable {

    private static final long serialVersionUID = 2899076626005610034L;

    @NotNull
    private String username;
    @NotNull
    private String password;
}
