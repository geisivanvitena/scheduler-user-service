package com.geisivan.userservice.infrastructure.exception.custom;

public class EnvironmentVariableNotFoundException extends IllegalStateException {

    public EnvironmentVariableNotFoundException(String variableName) {
        super("Required environment variable not set: " + variableName);
    }
}
