package com.geisivan.userservice.infrastructure.exception.custom;

import org.springframework.security.core.AuthenticationException;

public class UserInactiveException extends AuthenticationException {

    public UserInactiveException(String message) {
        super(message);
    }
}
