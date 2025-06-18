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

import static org.springframework.security.config.Customizer.withDefaults;

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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // --- RUTAS PÚBLICAS ---
                        .requestMatchers("/api/v1/autentificacion/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()

                        // --- RUTAS DE USUARIO (USER o ADMIN) ---
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pedidos/{pedidoId}/estado").hasRole("ADMIN")
                        .requestMatchers("/api/v1/pedidos/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/notificaciones/**").hasAnyRole("USER", "ADMIN")
                        // REGLAS ESPECÍFICAS: Mantenemos estas para que los pedidos funcionen
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventarios/producto/{productoId}/disminuir").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventarios/producto/{productoId}/incrementar").hasAnyRole("USER", "ADMIN")

                        // --- RUTAS DE ADMINISTRADOR (SOLO ADMIN) ---
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                        // REGLA GENERAL: La regla general para inventario ahora se encarga de todo lo demás (GETs, POSTs, etc.)
                        .requestMatchers("/api/v1/inventarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasRole("ADMIN")

                        // --- CUALQUIER OTRA RUTA ---
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return http.build();
    }
}