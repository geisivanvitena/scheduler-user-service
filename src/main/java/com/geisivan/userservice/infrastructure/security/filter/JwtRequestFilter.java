package com.geisivan.userservice.infrastructure.security.filter;

import com.geisivan.userservice.infrastructure.exception.custom.UserInactiveException;
import com.geisivan.userservice.infrastructure.security.jwt.JwtUtil;
import com.geisivan.userservice.infrastructure.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtRequestFilter(JwtUtil jwtUtil,
                            UserDetailsServiceImpl userDetailsService) {

        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain

    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {

            log.debug("No JWT token provided | method: {} | path: {}",
                    request.getMethod(),
                    request.getRequestURI());

            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(7).trim();

        var claimsOpt = jwtUtil.extractValidClaims(token);

        if (claimsOpt.isEmpty()) {

            log.warn("Invalid or expired JWT token | method: {} | path: {} | ip: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());

            filterChain.doFilter(request, response);
            return;
        }
        var claims = claimsOpt.get();
        var userIdOpt = jwtUtil.extractUserId(claims);

        var securityContext = SecurityContextHolder.getContext();

        if (userIdOpt.isPresent() && securityContext.getAuthentication() == null) {
            var userId = userIdOpt.get();
            var userDetails = userDetailsService.loadUserById(userId);

            if (!userDetails.isEnabled()) {
                log.warn("User is not enabled | userId: {} | method: {} | path: {}",
                        userId,
                        request.getMethod(),
                        request.getRequestURI());

                throw new UserInactiveException("User account is inactive");
            }
            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            securityContext.setAuthentication(authentication);

            log.debug("User authenticated via JWT | userId: {} | method: {} | path: {}",
                    userId,
                    request.getMethod(),
                    request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }
}
