package com.authorization.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAuthenticationConfig {

    @Value("${application.security.jwt.expiration}")
    private int expiration;

    @Value("${application.security.jwt.secret}")
    private String secret;

    public int getExpiration() {
        return expiration;
    }

    public String getSecret() {
        return secret;
    }
}
