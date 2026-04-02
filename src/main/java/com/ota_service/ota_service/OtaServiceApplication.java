package com.ota_service.ota_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OtaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtaServiceApplication.class, args);

	}

}
