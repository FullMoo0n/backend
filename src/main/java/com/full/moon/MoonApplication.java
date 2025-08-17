package com.full.moon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MoonApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoonApplication.class, args);
	}

}
