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



@RestController
@RequestMapping("/api/v1/autentificacion")
public class AutentificacionController {


    private final UsuarioService usuarioService;

    @Autowired
    public AutentificacionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

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

            UsuarioModel respuestaUsuario = new UsuarioModel();
            respuestaUsuario.setId(usuarioCreado.getId());
            respuestaUsuario.setNombre(usuarioCreado.getNombre());
            respuestaUsuario.setEmail(usuarioCreado.getEmail());
            respuestaUsuario.setRoles(usuarioCreado.getRoles());
            return new ResponseEntity<>(respuestaUsuario, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Datos inválidos para el registro: " + e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar el registro del usuario. Por favor, intente más tarde.");
        }
    }
}