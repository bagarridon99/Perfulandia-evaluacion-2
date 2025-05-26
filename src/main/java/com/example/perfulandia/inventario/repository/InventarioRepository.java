package com.example.perfulandia.inventario.repository;

import com.example.perfulandia.model.InventarioModel; // Asegúrate que la ruta a tu InventarioModel sea correcta
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioModel, Long> {

}