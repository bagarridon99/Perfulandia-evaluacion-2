package com.example.perfulandia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults; // Mantenemos si lo usas en httpBasic()

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF (común para APIs REST)
                .authorizeHttpRequests(authz -> authz
                        // --- RUTAS DE SWAGGER UI Y OPENAPI DOCS ---
                        // Permite acceso público a todas las rutas de Swagger/OpenAPI
                        .requestMatchers(
                                "/swagger-ui.html", // Ruta principal de Swagger UI
                                "/swagger-ui/**",    // Recursos estáticos de Swagger UI (CSS, JS, etc.)
                                "/v3/api-docs/**",   // Archivo de especificación OpenAPI JSON/YAML
                                "/swagger-resources/**", // Recursos adicionales para Swagger
                                "/swagger-resources",    // Recursos adicionales para Swagger
                                "/configuration/**",     // Configuraciones de Swagger
                                "/webjars/**",           // Dependencias web de Swagger
                                "/api-docs/**"           // Otra ruta común para la especificación
                        ).permitAll()

                        // --- RUTAS PÚBLICAS / AUTENTICACIÓN ---
                        // La ruta de autentificación. Si tu endpoint es /api/v1/autentificacion/login, etc.
                        .requestMatchers("/api/v1/autentificacion/**").permitAll()
                        // Productos solo para GET (visualización pública)
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()


                        // --- RUTAS DE USUARIO (USER o ADMIN) ---
                        // Pedidos: PUT de estado es solo ADMIN, pero todo lo demás puede ser USER/ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pedidos/{pedidoId}/estado").hasRole("ADMIN") // Específico: solo admin puede cambiar el estado
                        .requestMatchers("/api/v1/pedidos/**").hasAnyRole("USER", "ADMIN") // General: user/admin pueden ver/gestionar sus pedidos
                        .requestMatchers("/api/v1/notificaciones/**").hasAnyRole("USER", "ADMIN")
                        // Inventario: incrementar/disminuir (suelen ser acciones de venta/compra)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventarios/producto/{productoId}/disminuir").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventarios/producto/{productoId}/incrementar").hasAnyRole("USER", "ADMIN")


                        // --- RUTAS DE ADMINISTRADOR (SOLO ADMIN) ---
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                        // Inventario: Resto de operaciones CRUD para inventario (GET, POST, DELETE)
                        .requestMatchers("/api/v1/inventarios/**").hasRole("ADMIN")
                        // Productos: POST, PUT, DELETE son para admin (ya el GET estaba permitido arriba)
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasRole("ADMIN")


                        // --- CUALQUIER OTRA RUTA NO ESPECIFICADA ---
                        // Cualquier otra solicitud requiere autenticación (si no coincide con las reglas anteriores)
                        .anyRequest().authenticated()
                )
                // Esto habilita la autenticación Basic HTTP. Si usas JWT, no lo usarías o lo complementarías.
                .httpBasic(withDefaults());

        return http.build();
    }
}