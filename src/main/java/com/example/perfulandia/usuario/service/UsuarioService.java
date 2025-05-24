package com.example.perfulandia.usuario.service;

import com.example.perfulandia.model.UsuarioModel;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    UsuarioModel anadirUsuario(UsuarioModel usuario);
    Optional<UsuarioModel> buscarUsuarioPorId(Long id);
    List<UsuarioModel> buscarTodosLosUsuarios();
    UsuarioModel editarUsuario(Long id, UsuarioModel usuarioConNuevosDatos);
    void eliminarUsuario(Long id);

    // --- NUEVO MÉTODO AÑADIDO ---
    /**
     * Busca un usuario por su dirección de email.
     * @param email El email del usuario a buscar.
     * @return Un Optional que contiene el UsuarioModel si se encuentra, o un Optional vacío.
     */
    Optional<UsuarioModel> buscarPorEmail(String email);
    // --- FIN NUEVO MÉTODO ---
}