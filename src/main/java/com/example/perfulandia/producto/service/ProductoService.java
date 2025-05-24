package com.example.perfulandia.producto.service;

import com.example.perfulandia.model.ProductoModel; // Importa tu entidad
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    /**
     * Añade un nuevo producto.
     * @param producto El producto a crear.
     * @return El producto creado (con su ID asignado).
     */
    ProductoModel anadirProducto(ProductoModel producto); // AÑADIR

    /**
     * Busca un producto por su ID.
     * @param id El ID del producto a buscar.
     * @return Un Optional que contiene el producto si se encuentra, o vacío si no.
     */
    Optional<ProductoModel> buscarProductoPorId(Long id); // BUSCAR (por ID)

    /**
     * Busca todos los productos.
     * @return Una lista de todos los productos.
     */
    List<ProductoModel> buscarTodosLosProductos(); // BUSCAR (todos)

    /**
     * Edita/Actualiza un producto existente.
     * @param id El ID del producto a actualizar.
     * @param productoConNuevosDatos El producto con la información actualizada.
     * @return El producto actualizado.
     * @throws RuntimeException si el producto no se encuentra.
     */
    ProductoModel editarProducto(Long id, ProductoModel productoConNuevosDatos); // EDITAR

    /**
     * Elimina un producto por su ID.
     * @param id El ID del producto a eliminar.
     * @throws RuntimeException si el producto no se encuentra.
     */
    void eliminarProducto(Long id); // ELIMINAR
}