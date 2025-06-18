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
        // --- CREACIÓN DEL USUARIO ADMINISTRADOR POR DEFECTO ---
        String adminEmail = "admin@admin.com";
        if (!usuarioRepository.findByEmail(adminEmail).isPresent()) {
            UsuarioModel admin = new UsuarioModel();
            admin.setNombre("admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin")); // Contraseña codificada
            admin.setRole(Role.ROLE_ADMIN); // Rol de ADMINISTRADOR
            usuarioRepository.save(admin);
            System.out.println("✅ ¡Usuario administrador por defecto 'admin' creado!");
            System.out.println("   Usuario (email): " + adminEmail);
            System.out.println("   Contraseña: admin");
        }

        // --- CREACIÓN DEL USUARIO NORMAL POR DEFECTO ---
        String userEmail = "user@user.com"; // Email para el usuario normal
        String userPassword = "user";       // Contraseña para el usuario normal

        if (!usuarioRepository.findByEmail(userEmail).isPresent()) {
            UsuarioModel user = new UsuarioModel();
            user.setNombre("usuario");
            user.setEmail(userEmail);
            user.setPassword(passwordEncoder.encode(userPassword)); // Contraseña codificada
            user.setRole(Role.ROLE_USER); // ¡Rol de USUARIO NORMAL!
            usuarioRepository.save(user);
            System.out.println("✅ ¡Usuario normal por defecto 'usuario' creado!");
            System.out.println("   Usuario (email): " + userEmail);
            System.out.println("   Contraseña: " + userPassword);
        }
    }
}