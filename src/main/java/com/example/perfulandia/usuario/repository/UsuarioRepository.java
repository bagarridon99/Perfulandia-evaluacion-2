package com.example.perfulandia.usuario.repository;

import com.example.perfulandia.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importa Optional

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {


    Optional<UsuarioModel> findByEmail(String email);

}