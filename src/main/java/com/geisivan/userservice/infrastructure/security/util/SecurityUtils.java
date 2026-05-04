package com.geisivan.userservice.infrastructure.security.util;

import com.geisivan.userservice.infrastructure.exception.UnauthorizedException;
import com.geisivan.userservice.infrastructure.security.auth.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils(){}

    public static UserPrincipal getUser(){
        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal user)) {
            throw new UnauthorizedException("Invalid authentication");
        }
        return user;
    }

    public static Long getUserId(){
        return getUser().id();
    }
    public static String getUserEmail(){
        return getUser().email();
    }
}
