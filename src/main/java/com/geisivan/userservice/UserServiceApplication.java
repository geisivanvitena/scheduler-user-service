package com.geisivan.userservice;

import com.geisivan.userservice.infrastructure.config.dotenv.DotenvLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		DotenvLoader.load();
		SpringApplication.run(UserServiceApplication.class, args);

		log.info("User Service started successfully.");
	}

}

