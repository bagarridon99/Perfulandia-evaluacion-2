package com.example.perfulandia.autentificacion.controller;

import com.example.perfulandia.model.Role;
import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- IMPORTANTE AÑADIR ESTE IMPORT
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/autentificacion")
public class AutentificacionController {

    private final UsuarioService usuarioService;

    @Autowired
    public AutentificacionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    // --- MÉTODO PARA AÑADIR ADMINISTRADORES (EL QUE PROBABLEMENTE FALTA) ---
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Le dice a Spring que solo un ADMIN puede usar este endpoint
    public ResponseEntity<?> anadirAdmin(@RequestBody UsuarioModel usuario) {
        // Asignamos explícitamente el rol de ADMIN antes de guardarlo.
        usuario.setRole(Role.ROLE_ADMIN);
        UsuarioModel nuevoAdmin = usuarioService.anadirUsuario(usuario);

        // Creamos una respuesta segura sin la contraseña
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("id", nuevoAdmin.getId());
        respuesta.put("nombre", nuevoAdmin.getNombre());
        respuesta.put("email", nuevoAdmin.getEmail());
        respuesta.put("role", nuevoAdmin.getRole());

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }


    // --- MÉTODO DE REGISTRO PÚBLICO (EL QUE YA TIENES Y FUNCIONA) ---
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioModel nuevoUsuario) {
        try {
            if (nuevoUsuario.getNombre() == null || nuevoUsuario.getNombre().trim().isEmpty() ||
                    nuevoUsuario.getEmail() == null || nuevoUsuario.getEmail().trim().isEmpty() ||
                    nuevoUsuario.getPassword() == null || nuevoUsuario.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Nombre, email y contraseña son requeridos y no pueden estar vacíos.");
            }

            if (usuarioService.buscarPorEmail(nuevoUsuario.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya está registrado.");
            }

            UsuarioModel usuarioCreado = usuarioService.anadirUsuario(nuevoUsuario);

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("id", usuarioCreado.getId());
            respuesta.put("nombre", usuarioCreado.getNombre());
            respuesta.put("email", usuarioCreado.getEmail());
            respuesta.put("role", usuarioCreado.getRole());

            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Datos inválidos para el registro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar el registro del usuario. Por favor, intente más tarde.");
        }
    }
}