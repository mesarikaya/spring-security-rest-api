package com.mes.springsecurityapi.security.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import javax.security.auth.Subject;

@Getter
public class JwtPreAuthenticationToken extends AbstractAuthenticationToken {
    private final String authToken;
    private final String bearerRequestHeader;
    private final String username;

    public JwtPreAuthenticationToken(final String authToken, final String bearerRequestHeader, final String username) {
        super(null);
        this.authToken = authToken;
        this.bearerRequestHeader = bearerRequestHeader;
        this.username = username;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
