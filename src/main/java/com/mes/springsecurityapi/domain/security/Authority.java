package com.mes.springsecurityapi.domain.security;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Created by mesar on 12/22/2020
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("authorities")
public class Authority {

    @Id
    private Integer id;

    private String permission;
}
