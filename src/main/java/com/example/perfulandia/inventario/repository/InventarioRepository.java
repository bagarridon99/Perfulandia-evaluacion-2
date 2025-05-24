package com.example.perfulandia.inventario.repository;

import com.example.perfulandia.model.InventarioModel; // Asegúrate que la ruta a tu InventarioModel sea correcta
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Le indica a Spring que esta es una interfaz de repositorio
public interface InventarioRepository extends JpaRepository<InventarioModel, Long> {
    // JpaRepository<InventarioModel, Long> significa:
    // - InventarioModel: La entidad que este repositorio manejará.
    // - Long: El tipo de dato del ID de InventarioModel. Si tu ID es de otro tipo, ajústalo aquí.

    // Por ahora, la dejamos vacía. JpaRepository ya nos da métodos CRUD básicos:
    // save(), findById(), findAll(), deleteById(), etc.

    // Más adelante, si necesitas consultas específicas para el inventario, las añadirías aquí.
    // Por ejemplo, para buscar el inventario asociado a un ID de producto específico (si no usas @MapsId directamente):
    // Optional<InventarioModel> findByProducto_Id(Long productoId);
    // O si el campo en InventarioModel que referencia a ProductoModel se llama 'producto':
    // Optional<InventarioModel> findByProducto(ProductoModel producto);
}