package com.example.perfulandia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.media.Schema; // Importar

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Representa un usuario registrado en la plataforma de Perfulandia.")
public class UsuarioModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario.", example = "1")
    private Long id;

    @Column(nullable = false, length = 150)
    @Schema(description = "Nombre completo del usuario.", example = "Juan Pérez")
    private String nombre;

    @Column(nullable = false, unique = true, length = 200)
    @Schema(description = "Dirección de correo electrónico única del usuario (usada como nombre de usuario para login).", example = "juan.perez@example.com")
    private String email;

    @Column(nullable = false, length = 255)
    // No incluir example para la contraseña para evitar exponerla
    @Schema(description = "Contraseña cifrada del usuario.")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Rol del usuario en el sistema (ROLE_USER o ROLE_ADMIN).", example = "ROLE_USER")
    private Role role;

    public UsuarioModel() {
    }

    public UsuarioModel(String nombre, String email, String password, Role role) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public void setPassword(String password) { this.password = password; }

    // --- MÉTODOS REQUERIDOS POR UserDetails PARA SPRING SECURITY ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}