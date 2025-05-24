package com.example.perfulandia.usuario.repository;

import com.example.perfulandia.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importa Optional

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    // Método para buscar un usuario por su dirección de email.
    // Spring Data JPA generará automáticamente la consulta SQL necesaria
    // basándose en el nombre del método.
    Optional<UsuarioModel> findByEmail(String email);

}