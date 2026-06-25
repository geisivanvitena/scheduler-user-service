package com.geisivan.userservice.infrastructure.config.dotenv;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class DotenvInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(
            @NotNull ConfigurableApplicationContext context) {

        DotenvLoader.load();
    }
}
