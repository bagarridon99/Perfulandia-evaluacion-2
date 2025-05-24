package com.example.perfulandia.config;

import com.example.perfulandia.autentificacion.service.UserDetailsServiceImpl; // Asegúrate que la ruta sea correcta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Para especificar métodos HTTP
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Para APIs stateless (opcional por ahora)
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults; // Para usar configuraciones por defecto

@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring Security
public class SecurityConfig {

    // Tu UserDetailsService personalizado que creamos antes
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Bean para el encriptador de contraseñas (ya lo tenías)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para el AuthenticationManager (necesario para configurar el UserDetailsService y PasswordEncoder)
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
                // Deshabilitar CSRF (Cross-Site Request Forgery)
                // Es común para APIs REST stateless que no usan sesiones/cookies para autenticación de la misma forma que las apps web tradicionales.
                // Si vas a usar autenticación basada en tokens (como JWT) o si tu frontend no es una app web tradicional en el mismo origen, es seguro deshabilitarlo.
                // Si usas el formLogin por defecto de Spring Security con sesiones, podrías necesitar mantener CSRF habilitado o configurarlo adecuadamente.
                // Por simplicidad para empezar con una API REST, lo deshabilitamos.
                .csrf(csrf -> csrf.disable())

                // Configuración de autorización de peticiones
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos (no requieren autenticación)
                        .requestMatchers("/api/v1/autentificacion/**").permitAll() // Para registrar y hacer login
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll() // Permitir ver productos a todos
                        .requestMatchers(HttpMethod.GET, "/api/v1/inventarios/producto/**").permitAll() // Permitir ver inventario/stock a todos

                        // Endpoints del Actuator (opcional: protegerlos o exponer solo algunos)
                        // .requestMatchers("/actuator/**").permitAll() // O protegerlos: .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // Todas las demás peticiones requieren autenticación
                        .anyRequest().authenticated()
                )

                // Configurar HTTP Basic Authentication (opcional, pero útil para pruebas rápidas de API)
                .httpBasic(withDefaults())

                // Configurar Form Login (opcional, si quieres una página de login generada por Spring)
                // Si lo habilitas, Spring Security proveerá un endpoint /login y /logout por defecto.
                .formLogin(withDefaults());
        // Si usaras una API completamente stateless (ej. con JWT), configurarías la gestión de sesión así:
        // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}