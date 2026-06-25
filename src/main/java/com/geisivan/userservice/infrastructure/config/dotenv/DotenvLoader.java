package com.geisivan.userservice.infrastructure.config.dotenv;

import com.geisivan.userservice.infrastructure.exception.EnvironmentVariableNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DotenvLoader {

    private DotenvLoader() {}

    private static final Dotenv dotenv = Dotenv
            .configure()
            .ignoreIfMissing()
            .load();

    public static void load() {

        log.info("Loading environment variables");

        // PostgreSQL
        set("DB_HOST");
        set("DB_PORT");
        set("POSTGRES_DB");
        set("POSTGRES_USER");
        set("POSTGRES_PASSWORD");

        // JWT
        set("JWT_SECRET");
        set("JWT_EXPIRATION_MS");

        // Default Admin
        set("ADMIN_NAME");
        set("ADMIN_EMAIL");
        set("ADMIN_PASSWORD");

        log.info("Environment variables loaded successfully");
    }

    private static void set(String key) {
        String value = dotenv.get(key);

        if (value == null || value.isBlank()) {
            throw new EnvironmentVariableNotFoundException(
                    "Required environment variable not set: " + key);
        }
        System.setProperty(key, value);
    }
}
