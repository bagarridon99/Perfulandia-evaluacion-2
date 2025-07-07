package com.example.perfulandia.usuario.service;

import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.model.Role;
import com.example.perfulandia.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioModel usuario1;
    private UsuarioModel usuario2;

    @BeforeEach
    void setUp() {
        usuario1 = new UsuarioModel();
        usuario1.setId(1L);
        usuario1.setNombre("Usuario de Prueba 1");
        usuario1.setEmail("test1@example.com");
        usuario1.setPassword("password123");
        usuario1.setRole(Role.ROLE_USER);

        usuario2 = new UsuarioModel();
        usuario2.setId(2L);
        usuario2.setNombre("Usuario de Prueba 2");
        usuario2.setEmail("test2@example.com");
        usuario2.setPassword("password456");
        usuario2.setRole(Role.ROLE_ADMIN);
    }

    @Test
    void anadirUsuario_debeCodificarPasswordYGuardar() {
        // Arrange
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuario1);

        // Act
        UsuarioModel usuarioGuardado = usuarioService.anadirUsuario(usuario1);

        // Assert
        assertNotNull(usuarioGuardado);
        assertEquals("encodedPassword", usuarioGuardado.getPassword());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(usuarioRepository, times(1)).save(usuario1);
    }

    @Test
    void buscarTodosLosUsuarios_debeDevolverListaDeUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));

        // Act
        List<UsuarioModel> usuarios = usuarioService.buscarTodosLosUsuarios();

        // Assert
        assertNotNull(usuarios);
        assertEquals(2, usuarios.size());
        assertEquals("Usuario de Prueba 1", usuarios.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void editarUsuario_cuandoExiste_debeActualizarDatos() {
        // Arrange
        UsuarioModel datosNuevos = new UsuarioModel();
        datosNuevos.setNombre("Nombre Actualizado");
        datosNuevos.setPassword("newPassword");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuario1);

        // Act
        UsuarioModel usuarioActualizado = usuarioService.editarUsuario(1L, datosNuevos);

        // Assert
        assertNotNull(usuarioActualizado);
        assertEquals("Nombre Actualizado", usuarioActualizado.getNombre());
        assertEquals("encodedNewPassword", usuarioActualizado.getPassword());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(UsuarioModel.class));
    }

    @Test
    void editarUsuario_cuandoNoExiste_debeLanzarExcepcion() {
        // Arrange
        UsuarioModel datosNuevos = new UsuarioModel();
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioService.editarUsuario(99L, datosNuevos);
        });
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void eliminarUsuario_cuandoExiste_debeLlamarDeleteById() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        // Act & Assert
        assertDoesNotThrow(() -> usuarioService.eliminarUsuario(1L));
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarUsuario_cuandoNoExiste_debeLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioService.eliminarUsuario(99L);
        });
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}