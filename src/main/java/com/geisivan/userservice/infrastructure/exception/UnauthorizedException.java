package com.geisivan.userservice.infrastructure.exception;

import org.springframework.security.core.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {
    public UnauthorizedException(String message) {super(message);}

}