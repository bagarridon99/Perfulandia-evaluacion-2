package com.example.perfulandia.config;

import com.example.perfulandia.autentificacion.service.UserDetailsServiceImpl; // Asegúrate que la ruta sea correcta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy; // Importa Lazy si la usas en el constructor
import org.springframework.http.HttpMethod; // Para especificar métodos HTTP
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy; // Para APIs stateless, si lo usas más adelante
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults; // Para usar configuraciones por defecto

@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring Security
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    // Si aplicaste @Lazy a UserDetailsServiceImpl aquí para resolver un ciclo, mantenlo.
    // Si el @Lazy lo pusiste en UsuarioServiceImpl para el PasswordEncoder, entonces no es necesario aquí.
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
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
                .authorizeHttpRequests(authz -> authz
                        // --- Endpoints Públicos ---
                        .requestMatchers("/api/v1/autentificacion/**").permitAll() // Registro, Login (si es personalizado)
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll() // Ver productos
                        .requestMatchers(HttpMethod.GET, "/api/v1/inventarios/producto/**").permitAll() // Ver stock/inventario

                        // --- Endpoints de Pedidos ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").authenticated() // Crear un pedido requiere autenticación
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/mis-pedidos").authenticated() // Ver mis pedidos requiere autenticación
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/{pedidoId}").authenticated() // Ver un pedido específico requiere autenticación
                        // Para actualizar estado, solo ADMIN (asegúrate de tener usuarios con ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pedidos/{pedidoId}/estado").hasRole("ADMIN")

                        // --- Endpoints de Actuator (Opcional) ---
                        // .requestMatchers("/actuator/**").permitAll() // O .hasRole("ADMIN") si quieres protegerlos

                        // --- Cualquier otra petición requiere autenticación ---
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()) // Habilitar HTTP Basic Auth
                .formLogin(withDefaults()); // Habilitar Form Login por defecto
        // Para APIs stateless con JWT, la gestión de sesión sería diferente:
        // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}