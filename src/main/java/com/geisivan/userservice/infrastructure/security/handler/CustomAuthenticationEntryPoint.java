package com.geisivan.userservice.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geisivan.userservice.infrastructure.exception.code.ErrorCode;
import com.geisivan.userservice.infrastructure.exception.custom.UserInactiveException;
import com.geisivan.userservice.infrastructure.exception.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String UNAUTHORIZED_ERROR = "Unauthorized";
    private static final String FORBIDDEN_ERROR = "Forbidden";
    private static final String UNAUTHORIZED_MESSAGE =
            "Authentication is required to access this resource.";

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException authException

    ) throws IOException {

        String path = request.getRequestURI();

        if (!"/favicon.ico".equals(path)) {
            log.warn("Authentication failure | path: {} | message: {}",
                    request.getRequestURI(),
                    authException.getMessage());
        }

        int status;
        String error;
        String message;
        ErrorCode errorCode;

        if (authException instanceof UserInactiveException) {
            status = HttpServletResponse.SC_FORBIDDEN;
            error = FORBIDDEN_ERROR;
            message = authException.getMessage();
            errorCode = ErrorCode.FORBIDDEN;

        } else {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            error = UNAUTHORIZED_ERROR;
            message = UNAUTHORIZED_MESSAGE;
            errorCode = ErrorCode.UNAUTHORIZED;
        }

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status,
                error,
                message,
                request.getRequestURI(),
                errorCode,
                List.of());

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
