package com.authorization.security;

import com.authorization.config.SecurityConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
public class JwtTokenProvider implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    SecurityConfig securityConfig;

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date expiryDate = new Date(new Date().getTime() + securityConfig.getExpiration());
        Claims claims = Jwts.claims().setSubject(user.getUsername());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, securityConfig.getSecret())
                .compact();
    }
}