package com.geisivan.userservice;

import com.geisivan.userservice.infrastructure.config.dotenv.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		DotenvLoader.load();
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
