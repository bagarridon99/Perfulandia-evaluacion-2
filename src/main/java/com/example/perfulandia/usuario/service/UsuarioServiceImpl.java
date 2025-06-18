package com.example.perfulandia.usuario.service;

import com.example.perfulandia.model.Role;
import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              @Lazy PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioModel anadirUsuario(UsuarioModel usuario) {
        if (usuario.getRole() == null) {
            usuario.setRole(Role.ROLE_USER);
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioModel> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioModel> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioModel> buscarTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // --- MÉTODO ACTUALIZADO ---
    @Override
    @Transactional
    public UsuarioModel editarUsuario(Long id, UsuarioModel usuarioConNuevosDatos) {
        // 1. Busca el usuario existente en la base de datos.
        UsuarioModel usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        // 2. Actualiza el nombre SOLO si se proporciona uno nuevo en el JSON.
        if (usuarioConNuevosDatos.getNombre() != null && !usuarioConNuevosDatos.getNombre().trim().isEmpty()) {
            usuarioExistente.setNombre(usuarioConNuevosDatos.getNombre());
        }

        // 3. Actualiza el email SOLO si se proporciona uno nuevo en el JSON.
        if (usuarioConNuevosDatos.getEmail() != null && !usuarioConNuevosDatos.getEmail().trim().isEmpty()) {
            usuarioExistente.setEmail(usuarioConNuevosDatos.getEmail());
        }

        // 4. Actualiza la contraseña SOLO si se proporciona una nueva.
        if (usuarioConNuevosDatos.getPassword() != null && !usuarioConNuevosDatos.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioConNuevosDatos.getPassword()));
        }

        // 5. (Opcional) Actualiza el rol SOLO si se proporciona uno nuevo.
        if (usuarioConNuevosDatos.getRole() != null) {
            usuarioExistente.setRole(usuarioConNuevosDatos.getRole());
        }

        // 6. Guarda el usuario con solo los campos que fueron actualizados.
        return usuarioRepository.save(usuarioExistente);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}