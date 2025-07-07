package com.example.perfulandia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // ¡Nueva importación!
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // Implementamos esta interfaz

@Configuration
public class WebConfig implements WebMvcConfigurer { // La clase WebConfig ahora implementa WebMvcConfigurer directamente

    // Método para configurar CORS (tal como lo tenías)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    // ¡NUEVO MÉTODO PARA MANEJAR RECURSOS ESTÁTICOS DE SWAGGER UI!
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Rutas de Swagger UI
        registry.addResourceHandler("/swagger-ui.html**")
                .addResourceLocations("classpath:/META-INF/resources/");

        // Rutas de webjars (para los recursos JS/CSS de Swagger UI)
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // Rutas de v3/api-docs (para la especificación JSON/YAML)
        // Springdoc-openapi lo genera, pero asegúrate de que también esté mapeado
        registry.addResourceHandler("/v3/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/v3/api-docs/");

        // Si tienes /api-docs también (versiones anteriores de Swagger/OpenAPI)
        registry.addResourceHandler("/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/api-docs/");

        // Asegurarse de que los recursos de configuración de Swagger también sean accesibles
        registry.addResourceHandler("/configuration/**")
                .addResourceLocations("classpath:/META-INF/resources/configuration/");
        registry.addResourceHandler("/swagger-resources/**")
                .addResourceLocations("classpath:/META-INF/resources/swagger-resources/");

        // Muy importante: Permitir que el manejador por defecto de Spring Boot siga manejando otros recursos estáticos.
        // Esto es esencial si tienes otros archivos estáticos en src/main/resources/static o /public.
        // Si no se añade, podría causar que otros recursos estáticos no funcionen.
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/", "classpath:/resources/",
                        "classpath:/static/", "classpath:/public/");

    }
}