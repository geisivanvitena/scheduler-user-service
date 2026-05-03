package com.geisivan.userservice.infrastructure.config.dotenv;

import com.geisivan.userservice.infrastructure.exception.EnvironmentVariableNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;

public class DotenvLoader {

    private DotenvLoader(){}

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static void  load(){
        set("DB_HOST");
        set("DB_PORT");
        set("POSTGRES_DB");
        set("POSTGRES_USER");
        set("POSTGRES_PASSWORD");
        set("JWT_SECRET");
        set("JWT_EXPIRATION_MS");
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