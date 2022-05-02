package com.mes.springsecurityapi.domain.security.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by mesar on 12/30/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRoleAndAuthoritiesDTO implements Serializable {

    private static final long serialVersionUID = 9181414892285658488L;

    @NotNull
    private String userId;
    @NotNull
    private String username;
    @NotNull
    private String user_password;
    private String roleId;
    private String roleName;
    private String authId;
    private String authPermission;
}
