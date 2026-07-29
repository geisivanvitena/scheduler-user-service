package com.geisivan.userservice.infrastructure.config.openapi;

import com.geisivan.userservice.infrastructure.security.config.SecurityConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(){

        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0")
                        .description("""
                                        REST API responsible for:
                                        - User registration
                                        - Authentication
                                        - User management
                                        """))

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SecurityConfig.SECURITY_SCHEME))

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SecurityConfig.SECURITY_SCHEME,
                                        new SecurityScheme()
                                                .name(SecurityConfig.SECURITY_SCHEME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
    }
}
