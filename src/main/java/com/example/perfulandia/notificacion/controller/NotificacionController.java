package com.example.perfulandia.notificacion.controller;

import com.example.perfulandia.model.NotificacionModel;
import com.example.perfulandia.notificacion.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Para obtener el usuario autenticado
import org.springframework.security.core.userdetails.UserDetails; // Para obtener el email/username
import org.springframework.web.bind.annotation.*;

import java.util.HashMap; // Para el contador
import java.util.List;
import java.util.Map; // Para el contador
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Autowired
    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerMisNotificaciones(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o detalles de usuario no disponibles.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailUsuario = userDetails.getUsername();

        List<NotificacionModel> notificaciones = notificacionService.obtenerNotificacionesPorUsuario(emailUsuario);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<?> obtenerMisNotificacionesNoLeidas(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o detalles de usuario no disponibles.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailUsuario = userDetails.getUsername();

        List<NotificacionModel> notificacionesNoLeidas = notificacionService.obtenerNotificacionesNoLeidasPorUsuario(emailUsuario);
        return ResponseEntity.ok(notificacionesNoLeidas);
    }

    @GetMapping("/no-leidas/contador")
    public ResponseEntity<?> contarMisNotificacionesNoLeidas(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o detalles de usuario no disponibles.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailUsuario = userDetails.getUsername();

        long contador = notificacionService.contarNotificacionesNoLeidasPorUsuario(emailUsuario);
        Map<String, Long> respuesta = new HashMap<>();
        respuesta.put("noLeidas", contador);
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{notificacionId}/leida")
    public ResponseEntity<?> marcarNotificacionComoLeida(@PathVariable Long notificacionId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o detalles de usuario no disponibles.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailUsuario = userDetails.getUsername();

        try {
            Optional<NotificacionModel> notificacionActualizadaOpt = notificacionService.marcarNotificacionComoLeida(notificacionId, emailUsuario);
            if (notificacionActualizadaOpt.isPresent()) {
                return ResponseEntity.ok(notificacionActualizadaOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificación no encontrada o ya estaba marcada como leída.");
            }
        } catch (SecurityException e) { // Si el usuario no tiene permiso
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) { // Otros errores
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al marcar la notificación como leída.");
        }
    }

    @PutMapping("/marcar-todas-leidas")
    public ResponseEntity<?> marcarTodasComoLeidas(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o detalles de usuario no disponibles.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailUsuario = userDetails.getUsername();

        int cantidadMarcadas = notificacionService.marcarTodasLasNotificacionesComoLeidas(emailUsuario);
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", cantidadMarcadas + " notificaciones marcadas como leídas.");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{notificacionId}")
    public ResponseEntity<?> eliminarNotificacion(@PathVariable Long notificacionId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o detalles de usuario no disponibles.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailUsuario = userDetails.getUsername();

        try {
            notificacionService.eliminarNotificacion(notificacionId, emailUsuario);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) { // Por si la notificación no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}