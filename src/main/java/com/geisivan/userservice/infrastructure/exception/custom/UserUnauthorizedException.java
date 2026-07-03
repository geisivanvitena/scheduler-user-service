package com.geisivan.userservice.infrastructure.exception.custom;

import org.springframework.security.core.AuthenticationException;

public class UserUnauthorizedException extends AuthenticationException {

    public UserUnauthorizedException(String message) {super(message);}
}
