package com.geisivan.userservice.infrastructure.config.dotenv;

import com.geisivan.userservice.infrastructure.exception.custom.EnvironmentVariableNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public final class DotenvLoader {

    private DotenvLoader() {}

    private static final Dotenv DOTENV = Dotenv
            .configure()
            .ignoreIfMissing()
            .load();

    private static final List<String> REQUIRED_VARIABLES = List.of(
            "DB_HOST",
            "DB_PORT",
            "POSTGRES_DB",
            "POSTGRES_USER",
            "POSTGRES_PASSWORD",
            "JWT_SECRET",
            "JWT_EXPIRATION_MS",
            "ADMIN_NAME",
            "ADMIN_EMAIL",
            "ADMIN_PASSWORD"
    );

    public static void load() {
        log.info("Loading required environment variables");

        REQUIRED_VARIABLES.forEach(DotenvLoader::setSystemProperty);

        log.info("All required environment variables loaded successfully");
    }

    private static void setSystemProperty(String key) {
        String value = DOTENV.get(key);

        if (value == null || value.isBlank()) {
            log.error("Required environment variable '{}' is missing", key);

            throw new EnvironmentVariableNotFoundException(key);
        }
        System.setProperty(key, value);
    }
}
