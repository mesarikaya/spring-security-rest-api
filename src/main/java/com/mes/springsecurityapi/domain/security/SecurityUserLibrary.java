package com.mes.springsecurityapi.domain.security;

import com.mes.springsecurityapi.domain.security.DTO.UserRoleAndAuthoritiesDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mesar on 12/24/2020
 */
@Slf4j
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Component
public class SecurityUserLibrary extends User implements UserDetails {

    private Set<UserRoleAndAuthoritiesDTO> userRoleAndAuths;

    public SecurityUserLibrary(User user, Set<UserRoleAndAuthoritiesDTO> userRoleAndAuths){
        super(user);
        this.userRoleAndAuths = userRoleAndAuths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();
        userRoleAndAuths.stream()
                .forEachOrdered(rolesAndAuths ->{
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesAndAuths.getRoleName()));
                    authorities.add(new SimpleGrantedAuthority(rolesAndAuths.getAuthPermission()));
                });
        log.info("Registered user roles and auths: {}", authorities);
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return getAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return getAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return getCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return getEnabled();
    }
}
