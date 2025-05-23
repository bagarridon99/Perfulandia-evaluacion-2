package com.example.perfulandia.producto.repository;

import com.example.perfulandia.model.ProductoModel; //
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoModel, Long> {

    // Con solo extender JpaRepository<ProductoModel, Long>,
    // ya obtienes automáticamente métodos como:
    // - save(ProductoModel entity) -> para guardar o actualizar
    // - findById(Long id) -> para buscar por ID
    // - findAll() -> para obtener todos los productos
    // - deleteById(Long id) -> para eliminar por ID
    // - count() -> para contar cuántos productos hay
    // - existsById(Long id) -> para verificar si un producto existe
    // ...y muchos más!

    // --- Métodos de Consulta Personalizados (Opcionales) ---
    // Spring Data JPA también te permite definir tus propios métodos de consulta
    // simplemente nombrando los métodos de una manera específica.
    // Ejemplos (puedes añadirlos si los necesitas más adelante):

    // List<ProductoModel> findByCategoria(String categoria);

    // List<ProductoModel> findByMarca(String marca);

    // List<ProductoModel> findByNombreContainingIgnoreCase(String nombre);

}