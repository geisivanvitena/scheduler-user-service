package com.geisivan.userservice.infrastructure.security.util;

import com.geisivan.userservice.infrastructure.exception.custom.UserUnauthorizedException;
import com.geisivan.userservice.infrastructure.security.auth.MainUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    public static MainUser getUser() {
        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserUnauthorizedException("User not authenticated");
        }

        var principal = authentication.getPrincipal();

        if (!(principal instanceof MainUser user)) {
            throw new UserUnauthorizedException("Invalid authentication principal");
        }
        return user;
    }

    public static Long getUserId() {
        return getUser().id();
    }

    public static String getUserEmail() {
        return getUser().email();
    }
}
