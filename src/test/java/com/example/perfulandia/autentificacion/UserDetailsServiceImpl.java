package com.example.perfulandia.autentificacion.service;

import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.model.Role;
import com.example.perfulandia.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private UsuarioModel usuario;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setEmail("user@example.com");
        usuario.setPassword("encodedPassword"); // Simulamos que la contraseña ya está codificada
        usuario.setRole(Role.ROLE_USER);
    }

    @Test
    void loadUserByUsername_cuandoUsuarioExiste_debeDevolverUserDetails() {
        // Arrange
        when(usuarioService.buscarPorEmail("user@example.com")).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
        verify(usuarioService, times(1)).buscarPorEmail("user@example.com");
    }

    @Test
    void loadUserByUsername_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        // Arrange
        when(usuarioService.buscarPorEmail("nouser@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nouser@example.com");
        });
    }
}