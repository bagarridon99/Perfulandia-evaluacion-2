package com.example.perfulandia.usuario.service;

import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// En una aplicación real, aquí inyectarías un PasswordEncoder
// import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    // private final PasswordEncoder passwordEncoder; // Descomentar si implementas encriptación

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) { // Añadir PasswordEncoder passwordEncoder si lo usas
        this.usuarioRepository = usuarioRepository;
        // this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioModel anadirUsuario(UsuarioModel usuario) {
        // En una aplicación real, encriptarías la contraseña antes de guardarla:
        // usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioModel> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
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

        if (usuarioConNuevosDatos.getPassword() != null && !usuarioConNuevosDatos.getPassword().isEmpty()) {

            usuarioExistente.setPassword(usuarioConNuevosDatos.getPassword()); // Sin encriptar para este ejemplo
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
