package com.authorization.security;

import com.authorization.config.SecurityConfig;
import com.authorization.exception.AuthorizationHeaderMissingException;
import com.authorization.exception.ExpiredJwtTokenException;
import com.authorization.exception.InvalidJwtTokenException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;


@Slf4j
@Component
public class SecurityUtility {

    private final SecurityConfig securityConfig;

    public SecurityUtility(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    private HttpServletRequest getRequestFromContext() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String header = getAuthorizationHeader(request);
        return header.split(" ")[1].trim();
    }

    private String getAuthorizationHeader(HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
            throw new AuthorizationHeaderMissingException();
        }
        return header;
    }

    private Map<String, Object> getClaimsFromToken(HttpServletRequest request) {
        String token = getTokenFromHeader(request);
        try {
            var signedJwt = SignedJWT.parse(token);
            if (signedJwt.verify(new MACVerifier(securityConfig.getSecret()))) {
                throw new InvalidJwtTokenException("token was not signed with correct secret");
            }
            return signedJwt.getPayload().toJSONObject();
        } catch (ParseException | JOSEException e) {
            log.error("could not parse jwt token");
            return Collections.emptyMap();
        }
    }

    public void checkIfTokenIsExpired() {
        HttpServletRequest request = getRequestFromContext();
        Map<String, Object> claims = getClaimsFromToken(request);
        if (!(claims.get("exp") instanceof Long)) {
            throw new InvalidJwtTokenException("Token does not have 'exp' claim");
        }
        Long expiresAt = (Long) claims.get("exp");

        var expiresAtDate = Instant.ofEpochSecond(expiresAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        if (expiresAtDate.isBefore(LocalDateTime.now())) {
            log.error("token is expired");
            throw new ExpiredJwtTokenException();
        }
    }

    public String getUserName() {
        HttpServletRequest request = getRequestFromContext();
        Map<String, Object> claims = getClaimsFromToken(request);
        if (!(claims.get("sub") instanceof String)) {
            throw new InvalidJwtTokenException("Token does not have 'exp' claim");
        }
        return String.valueOf(claims.get("sub"));
    }
}
