package com.example.cokathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CokathonApplication {

	public static void main(String[] args) {
		SpringApplication.run(CokathonApplication.class, args);
	}

}
