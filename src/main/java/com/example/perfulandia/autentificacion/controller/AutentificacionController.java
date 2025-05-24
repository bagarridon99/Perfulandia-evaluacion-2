package com.example.perfulandia.autentificacion.controller;

import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Para logging, si decides añadirlo:
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/autentificacion")
public class AutentificacionController {

    // private static final Logger logger = LoggerFactory.getLogger(AutentificacionController.class);

    private final UsuarioService usuarioService;

    @Autowired
    public AutentificacionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioModel nuevoUsuario) {
        // System.out.println("--- REGISTRAR USUARIO ---");
        // System.out.println("Nombre recibido: " + (nuevoUsuario.getNombre() == null ? "null" : "'" + nuevoUsuario.getNombre() + "'"));
        // System.out.println("Email recibido: " + (nuevoUsuario.getEmail() == null ? "null" : "'" + nuevoUsuario.getEmail() + "'"));
        // System.out.println("Password (solo si está presente): " + (nuevoUsuario.getPassword() != null && !nuevoUsuario.getPassword().isEmpty() ? "Presente" : "Ausente o Vacío"));
        // System.out.println("Roles recibidos: " + nuevoUsuario.getRoles()); // Aunque no lo uses para el error actual

        try {
            // --- VALIDACIÓN MEJORADA ---
            if (nuevoUsuario.getNombre() == null || nuevoUsuario.getNombre().trim().isEmpty() ||
                    nuevoUsuario.getEmail() == null || nuevoUsuario.getEmail().trim().isEmpty() ||
                    nuevoUsuario.getPassword() == null || nuevoUsuario.getPassword().trim().isEmpty()) {
                // logger.warn("Faltan datos para el registro: Nombre, Email o Contraseña.");
                return ResponseEntity.badRequest().body("Nombre, email y contraseña son requeridos y no pueden estar vacíos.");
            }
            // Podrías añadir más validaciones aquí, como formato de email, longitud de contraseña, etc.,
            // o usar Jakarta Bean Validation en UsuarioModel.

            // Verificar si el email ya existe
            if (usuarioService.buscarPorEmail(nuevoUsuario.getEmail()).isPresent()) {
                // logger.warn("Intento de registro con email ya existente: {}", nuevoUsuario.getEmail());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya está registrado.");
            }

            UsuarioModel usuarioCreado = usuarioService.anadirUsuario(nuevoUsuario); // anadirUsuario ya encripta y asigna rol por defecto si es necesario

            // --- RESPUESTA MEJORADA (SIN CONTRASEÑA) ---
            UsuarioModel respuestaUsuario = new UsuarioModel();
            respuestaUsuario.setId(usuarioCreado.getId());
            respuestaUsuario.setNombre(usuarioCreado.getNombre());
            respuestaUsuario.setEmail(usuarioCreado.getEmail());
            respuestaUsuario.setRoles(usuarioCreado.getRoles()); // Para mostrar el rol que se asignó (ej. el por defecto)

            // logger.info("Usuario registrado con éxito: {}", respuestaUsuario.getEmail());
            return new ResponseEntity<>(respuestaUsuario, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) { // Captura validaciones de negocio o argumentos ilegales
            // logger.error("Argumento ilegal durante el registro: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Datos inválidos para el registro: " + e.getMessage());
        } catch (Exception e) { // Captura cualquier otra excepción inesperada
            // logger.error("Error inesperado al registrar el usuario: {}", e.getMessage(), e); // Es buena práctica loguear el stack trace completo
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar el registro del usuario. Por favor, intente más tarde.");
        }
    }
}