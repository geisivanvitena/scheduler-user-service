package com.geisivan.userservice.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geisivan.userservice.infrastructure.exception.code.ErrorCode;
import com.geisivan.userservice.infrastructure.exception.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException

    ) throws IOException {

        log.warn("Access denied | path: {} | message: {}",
                request.getRequestURI(),
                accessDeniedException.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "Access denied. You do not have permission to access this resource.",
                request.getRequestURI(),
                ErrorCode.FORBIDDEN,
                List.of());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
