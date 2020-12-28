package com.mes.springsecurityapi.domain.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by mesar on 12/24/2020
 */
@Slf4j
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Component
public class SecurityUserLibrary extends User implements UserDetails {

    private Set<Authority> authorities;
    public SecurityUserLibrary(User user, Set<Authority> authorities){
        super(user);
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return AuthorityUtils.commaSeparatedStringToAuthorityList(
                this.authorities.stream()
                        .map(authority -> authority.getPermission())
                        .collect(Collectors.joining(",")));
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
