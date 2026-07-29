package com.geisivan.userservice.infrastructure.security.util;

import com.geisivan.userservice.infrastructure.exception.custom.UserUnauthorizedException;
import com.geisivan.userservice.infrastructure.security.auth.MainUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserUtil {

    private AuthenticatedUserUtil() {}

    public static MainUser getUser() {
        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserUnauthorizedException(
                    "User not authenticated");
        }

        var main = authentication.getPrincipal();

        if (!(main instanceof MainUser user)) {
            throw new UserUnauthorizedException(
                    "Invalid authentication principal");
        }
        return user;
    }

    public static Long getId() {
        return getUser().id();
    }

    public static String getEmail() {
        return getUser().email();
    }
}
