package com.example.perfulandia.config;

import com.example.perfulandia.autentificacion.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Lazy; // Solo si lo necesitas para resolver ciclos
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // --- Endpoints Públicos ---
                        .requestMatchers("/api/v1/autentificacion/**").permitAll() // Registro, Login
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll() // Ver productos
                        .requestMatchers(HttpMethod.GET, "/api/v1/inventarios/producto/**").permitAll() // Ver stock/inventario

                        // --- NUEVAS REGLAS: Endpoints de Inventario para llamadas internas de Feign ---
                        // Permitimos PUT a estas rutas específicas para la comunicación interna simulada desde PedidosService.
                        // En un entorno real de microservicios, esto se manejaría con autenticación servicio-a-servicio.
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventarios/producto/{productoId}/disminuir").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventarios/producto/{productoId}/incrementar").permitAll()

                        // --- Endpoints de Pedidos (Requieren autenticación o roles específicos) ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/mis-pedidos").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/{pedidoId}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pedidos/{pedidoId}/estado").hasRole("ADMIN")

                        // --- Endpoints de Notificaciones (Requieren autenticación) ---
                        .requestMatchers("/api/v1/notificaciones/**").authenticated()

                        // --- Endpoints de Actuator (Opcional, ejemplo) ---
                        // .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // --- Cualquier otra petición requiere autenticación ---
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()) // Habilitar HTTP Basic Auth
                .formLogin(withDefaults()); // Habilitar Form Login por defecto
        // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}