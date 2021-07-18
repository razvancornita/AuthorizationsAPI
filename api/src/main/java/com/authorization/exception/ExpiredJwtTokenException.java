package com.authorization.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredJwtTokenException extends AuthenticationException {
    public ExpiredJwtTokenException() {
        super("Token is expired.");
    }
}
