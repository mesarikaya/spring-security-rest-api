package com.mes.springsecurityapi.security.services.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mesar on 12/23/2020
 */
@Service
public class SecretService {

    private Map<String, String> secrets = new HashMap<>();

    private SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
        @Override
        public SecretKey resolveSigningKey(JwsHeader header, Claims claims) {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secrets.get(header.getAlgorithm())));
        }
    };

    @PostConstruct
    public void setup() {
        refreshSecrets();
    }

    public SigningKeyResolver getSigningKeyResolver() {
        return signingKeyResolver;
    }

    public Map<String, String> getSecrets() {
        return secrets;
    }

    public void setSecrets(Map<String, String> secrets) {
        Assert.notNull(secrets, "Secrets cannot be null!");
        Assert.hasText(secrets.get(SignatureAlgorithm.HS256.getValue()), "Secrets should not be with no length!");
        Assert.hasText(secrets.get(SignatureAlgorithm.HS384.getValue()), "Secrets should not be with no length!");
        Assert.hasText(secrets.get(SignatureAlgorithm.HS512.getValue()), "Secrets should not be with no length!");

        this.secrets = secrets;
    }

    public SecretKey getHS256SecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secrets.get(SignatureAlgorithm.HS256.getValue())));
    }

    public SecretKey getHS384SecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secrets.get(SignatureAlgorithm.HS384.getValue())));
    }

    public SecretKey getHS512SecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secrets.get(SignatureAlgorithm.HS512.getValue())));
    }

    public Map<String, String> refreshSecrets() {

        SecretKey key = MacProvider.generateKey(SignatureAlgorithm.HS256);
        secrets.put(SignatureAlgorithm.HS256.getValue(), Encoders.BASE64.encode(key.getEncoded()));

        key = MacProvider.generateKey(SignatureAlgorithm.HS384);
        secrets.put(SignatureAlgorithm.HS384.getValue(), Encoders.BASE64.encode(key.getEncoded()));

        key = MacProvider.generateKey(SignatureAlgorithm.HS512);
        secrets.put(SignatureAlgorithm.HS512.getValue(), Encoders.BASE64.encode(key.getEncoded()));

        return secrets;
    }
}
