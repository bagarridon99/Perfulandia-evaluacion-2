package com.example.perfulandia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- 1. AÑADE ESTA IMPORTACIÓN

@SpringBootApplication
@EnableFeignClients // <-- 2. AÑADE ESTA ANOTACIÓN
public class PerfulandiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerfulandiaApplication.class, args);
	}

}