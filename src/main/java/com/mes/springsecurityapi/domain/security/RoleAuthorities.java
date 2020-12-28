package com.mes.springsecurityapi.domain.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Created by mesar on 12/25/2020
 */
@Slf4j
@Data
@AllArgsConstructor
@Builder
@Table("role_authorities")
public class RoleAuthorities {

    @Id
    private Integer id;

    private Integer roleId;

    private Integer authorityId;

    public static RoleAuthorities of(Role role, Authority authority) {
        return new RoleAuthorities(null, role.getId(), authority.getId());
    }
}