package com.example.perfulandia.inventario.service;

import com.example.perfulandia.model.InventarioModel;
import com.example.perfulandia.model.ProductoModel; // Necesitaremos ProductoModel para algunas operaciones

import java.util.Optional;

public interface InventarioService {

    /**
     * Obtiene el registro de inventario asociado a un ID de producto.
     * @param productoId El ID del producto.
     * @return Un Optional con el InventarioModel si existe, o un Optional vacío.
     */
    Optional<InventarioModel> obtenerInventarioPorProductoId(Long productoId);

    /**
     * Obtiene la cantidad de stock disponible para un producto.
     * Si no hay registro de inventario, podría devolver 0 o lanzar una excepción.
     * @param productoId El ID del producto.
     * @return La cantidad disponible.
     */
    int obtenerStockDisponible(Long productoId);

    /**
     * Asegura que un producto tenga un registro de inventario y establece su stock inicial.
     * Si ya existe un inventario para el producto, puede que solo actualice la cantidad o no haga nada si la lógica es solo para creación.
     * Si no existe, crea un nuevo registro de inventario para el producto.
     * @param producto El ProductoModel para el cual asegurar/crear el inventario.
     * @param stockInicial La cantidad inicial de stock.
     * @return El InventarioModel creado o existente.
     */
    InventarioModel asegurarInventarioParaProducto(ProductoModel producto, int stockInicial);

    /**
     * Incrementa el stock de un producto.
     * @param productoId El ID del producto cuyo stock se va a incrementar.
     * @param cantidadAIncrementar La cantidad a sumar al stock actual.
     * @return El InventarioModel actualizado.
     * @throws RuntimeException si el producto o su inventario no se encuentran.
     */
    InventarioModel incrementarStock(Long productoId, int cantidadAIncrementar);

    /**
     * Disminuye el stock de un producto.
     * Debería verificar si hay suficiente stock antes de disminuir.
     * @param productoId El ID del producto cuyo stock se va a disminuir.
     * @param cantidadADisminuir La cantidad a restar del stock actual.
     * @return El InventarioModel actualizado.
     * @throws RuntimeException si el producto o su inventario no se encuentran, o si no hay stock suficiente.
     */
    InventarioModel disminuirStock(Long productoId, int cantidadADisminuir);

    // Podrías tener un método más general para actualizar directamente si lo prefieres:
    // InventarioModel actualizarCantidadStock(Long productoId, int nuevaCantidadTotal);
}