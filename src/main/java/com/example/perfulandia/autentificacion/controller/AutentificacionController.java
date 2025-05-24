package com.example.perfulandia.autentificacion.controller;

import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.usuario.service.UsuarioService; // Importa tu UsuarioService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/autentificacion") // Ruta base que permitimos en SecurityConfig
public class AutentificacionController {

    private final UsuarioService usuarioService;

    @Autowired
    public AutentificacionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioModel nuevoUsuario) {
        try {
            // Validaciones básicas (podrías añadir más)
            if (nuevoUsuario.getEmail() == null || nuevoUsuario.getEmail().isEmpty() ||
                    nuevoUsuario.getPassword() == null || nuevoUsuario.getPassword().isEmpty() ||
                    nuevoUsuario.getNombre() == null || nuevoUsuario.getNombre().isEmpty()) {
                return ResponseEntity.badRequest().body("Nombre, email y contraseña son requeridos.");
            }

            // Verificar si el email ya existe (necesitarías añadir este método a UsuarioService)
            if (usuarioService.buscarPorEmail(nuevoUsuario.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya está registrado.");
            }

            UsuarioModel usuarioCreado = usuarioService.anadirUsuario(nuevoUsuario); // anadirUsuario ya encripta la contraseña
            // No devolvemos la contraseña en la respuesta
            usuarioCreado.setPassword(null);
            return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);

        } catch (Exception e) {
            // Loggear el error e.getMessage()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el usuario: " + e.getMessage());
        }
    }

    // NOTA: El endpoint de "/login" es manejado por Spring Security si usas formLogin.
    // Si quisieras un endpoint de login personalizado (ej. para devolver un token JWT),
    // lo implementarías aquí. Pero para empezar, con formLogin o httpBasic, no es estrictamente necesario.
}