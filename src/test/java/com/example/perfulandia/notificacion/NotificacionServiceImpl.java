package com.example.perfulandia.notificacion.service;

import com.example.perfulandia.model.NotificacionModel;
import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.model.Role;
import com.example.perfulandia.notificacion.repository.NotificacionRepository;
import com.example.perfulandia.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private UsuarioModel usuario;
    private NotificacionModel notificacion;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");

        notificacion = new NotificacionModel("Este es un mensaje de prueba", usuario);
        notificacion.setId(100L);
    }

    @Test
    void crearNotificacion_conDatosValidos_debeGuardarNotificacion() {
        // Arrange
        // CORRECCIÓN: Configuramos el mock para que devuelva la misma instancia que recibe.
        // Esto simula cómo JPA devuelve la entidad guardada.
        when(notificacionRepository.save(any(NotificacionModel.class))).thenAnswer(invocation -> {
            NotificacionModel notificacionGuardada = invocation.getArgument(0);
            notificacionGuardada.setId(101L); // Simulamos que la BD le asigna un nuevo ID
            return notificacionGuardada;
        });

        // Act: Llamamos al método con el mensaje que queremos probar
        NotificacionModel resultado = notificacionService.crearNotificacion(usuario, "Mensaje nuevo");

        // Assert
        assertNotNull(resultado);
        // Ahora 'resultado' tendrá el mensaje correcto porque el mock devolvió lo que se le pasó.
        assertEquals("Mensaje nuevo", resultado.getMensaje());
        assertEquals(usuario.getId(), resultado.getUsuarioDestinatario().getId());
        verify(notificacionRepository, times(1)).save(any(NotificacionModel.class));
    }

    @Test
    void obtenerNotificacionesPorUsuario_debeDevolverListaCorrecta() {
        // Arrange
        when(usuarioService.buscarPorEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findByUsuarioDestinatarioOrderByFechaCreacionDesc(usuario))
                .thenReturn(Arrays.asList(notificacion));

        // Act
        List<NotificacionModel> resultados = notificacionService.obtenerNotificacionesPorUsuario("test@example.com");

        // Assert
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals(100L, resultados.get(0).getId());
        verify(usuarioService, times(1)).buscarPorEmail("test@example.com");
        verify(notificacionRepository, times(1)).findByUsuarioDestinatarioOrderByFechaCreacionDesc(usuario);
    }

    @Test
    void marcarNotificacionComoLeida_cuandoEsPropietario_debeMarcarla() {
        // Arrange
        notificacion.setLeida(false);
        when(usuarioService.buscarPorEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findById(100L)).thenReturn(Optional.of(notificacion));

        // Act
        Optional<NotificacionModel> resultado = notificacionService.marcarNotificacionComoLeida(100L, "test@example.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().isLeida());
        verify(notificacionRepository, times(1)).save(notificacion);
    }

    @Test
    void marcarNotificacionComoLeida_cuandoNoEsPropietario_debeLanzarExcepcion() {
        // Arrange
        UsuarioModel otroUsuario = new UsuarioModel();
        otroUsuario.setId(2L);
        otroUsuario.setEmail("otro@example.com");

        when(usuarioService.buscarPorEmail("otro@example.com")).thenReturn(Optional.of(otroUsuario));
        when(notificacionRepository.findById(100L)).thenReturn(Optional.of(notificacion)); // La notificacion es del usuario 1

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            notificacionService.marcarNotificacionComoLeida(100L, "otro@example.com");
        });

        verify(notificacionRepository, never()).save(any());
    }
}