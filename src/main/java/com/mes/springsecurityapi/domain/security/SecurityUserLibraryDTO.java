package com.mes.springsecurityapi.domain.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * Created by mesar on 12/24/2020
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityUserLibraryDTO {

    private Integer id;
    private String username;
    private String password;
    private Set<Role> roles;
}
