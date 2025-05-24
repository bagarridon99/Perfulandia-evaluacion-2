package com.example.perfulandia.usuario.service;

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
        // --- INICIO DE CAMBIO/VERIFICACIÓN ---
        // Si el campo 'roles' en el objeto UsuarioModel que llega es nulo o vacío,
        // asignamos "ROLE_USER" por defecto.
        if (usuario.getRoles() == null || usuario.getRoles().trim().isEmpty()) {
            usuario.setRoles("ROLE_USER"); // Asigna un rol por defecto
        }
        // --- FIN DE CAMBIO/VERIFICACIÓN ---

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

        // Solo actualiza y encripta la contraseña si se proporciona una nueva y no está vacía
        if (usuarioConNuevosDatos.getPassword() != null && !usuarioConNuevosDatos.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioConNuevosDatos.getPassword()));
        }
        // Si quisieras actualizar roles aquí también, necesitarías una lógica similar:
        // if (usuarioConNuevosDatos.getRoles() != null && !usuarioConNuevosDatos.getRoles().trim().isEmpty()) {
        //     usuarioExistente.setRoles(usuarioConNuevosDatos.getRoles());
        // }
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