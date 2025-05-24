package com.example.perfulandia.usuario.service;

import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // <-- AÑADE ESTA IMPORTACIÓN
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
                              @Lazy PasswordEncoder passwordEncoder) { // <-- AÑADE @Lazy AQUÍ
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ... el resto de tus métodos (anadirUsuario, buscarPorEmail, etc.) permanecen igual ...
    // Asegúrate de que anadirUsuario y editarUsuario usen this.passwordEncoder.encode(...)

    @Override
    @Transactional
    public UsuarioModel anadirUsuario(UsuarioModel usuario) {
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

    @Override
    @Transactional
    public UsuarioModel editarUsuario(Long id, UsuarioModel usuarioConNuevosDatos) {
        UsuarioModel usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        usuarioExistente.setNombre(usuarioConNuevosDatos.getNombre());
        usuarioExistente.setEmail(usuarioConNuevosDatos.getEmail());

        if (usuarioConNuevosDatos.getPassword() != null && !usuarioConNuevosDatos.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioConNuevosDatos.getPassword()));
        }
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