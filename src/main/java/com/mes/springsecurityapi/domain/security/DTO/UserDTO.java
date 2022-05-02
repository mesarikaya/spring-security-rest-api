package com.mes.springsecurityapi.domain.security.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by mesar on 12/28/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public final class UserDTO implements Serializable {

    private static final long serialVersionUID = 1390765895082338835L;

    @NotNull
    private String firstName;

    @NotNull
    private String middleName;

    @NotNull
    private String lastName;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String confirmPassword;

    @NotNull
    private String streetName;

    @NotNull
    private String houseNumber;

    @NotNull
    private String city;

    @NotNull
    private String zipcode;

    @NotNull
    private String state;

    @NotNull
    private String country;

    private boolean validated;
    
    private boolean hasPasswordMatch;
}
