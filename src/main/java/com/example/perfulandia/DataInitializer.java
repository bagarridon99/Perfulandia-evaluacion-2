// Ubicación: src/main/java/com/example/perfulandia/DataInitializer.java
package com.example.perfulandia;

import com.example.perfulandia.model.Role;
import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // --- CREACIÓN DEL USUARIO CON EMAIL "admin@admin.com" ---

        // El email que solicitaste para el login
        String adminEmail = "admin@admin.com";

        if (!usuarioRepository.findByEmail(adminEmail).isPresent()) {

            UsuarioModel admin = new UsuarioModel();
            admin.setNombre("admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ROLE_ADMIN);

            usuarioRepository.save(admin);

            // Mensajes actualizados en la consola
            System.out.println("✅ ¡Usuario administrador por defecto 'admin' creado!");
            System.out.println("   Usuario (email): " + adminEmail);
            System.out.println("   Contraseña: admin");
        }
    }
}