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
}
