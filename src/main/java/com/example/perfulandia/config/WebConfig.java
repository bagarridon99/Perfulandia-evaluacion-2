package com.example.perfulandia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/v1/**") // Asegúrate que el patrón cubra tus endpoints
                        .allowedOrigins("*")       // Permite cualquier origen (para desarrollo)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // INCLUYE "OPTIONS"
                        .allowedHeaders("*")       // Permite todas las cabeceras
                        .allowCredentials(false);
                // .maxAge(3600); // Opcional: cuánto tiempo el navegador puede cachear la respuesta preflight
            }
        };
    }
}