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
@Table("user_roles")
public class UserRoles {

    @Id
    private Integer id;

    private Integer userId;

    private Integer roleId;

    public static UserRoles of(User user, Role role) {
        return new UserRoles(null, user.getId(), role.getId());
    }
}
