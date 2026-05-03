package com.geisivan.userservice.infrastructure.config.dotenv;

import io.micrometer.common.lang.NonNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class DotenvInitializer
        implements ApplicationContextInitializer
        <ConfigurableApplicationContext> {

    @Override
    public void initialize(
            @NonNull ConfigurableApplicationContext context) {
        DotenvLoader.load();
    }
}