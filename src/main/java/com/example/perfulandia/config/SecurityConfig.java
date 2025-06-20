package com.example.perfulandia.config;

import com.example.perfulandia.autentificacion.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
                        .requestMatchers(HttpMethod.OPTIONS, "/api/v1/**").permitAll()


                        .requestMatchers("/api/v1/autentificacion/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/inventarios/producto/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventarios/producto/{productoId}/disminuir").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventarios/producto/{productoId}/incrementar").permitAll()


                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/mis-pedidos").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/{pedidoId}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pedidos/{pedidoId}/estado").authenticated() // Cambiado a authenticated para simplificar si no tienes admins aún


                        .requestMatchers("/api/v1/notificaciones/**").authenticated()


                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults());

        return http.build();
    }
}