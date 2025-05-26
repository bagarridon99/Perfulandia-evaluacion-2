package com.example.perfulandia.inventario.service;

import com.example.perfulandia.model.InventarioModel;
import com.example.perfulandia.model.ProductoModel; // Necesitaremos ProductoModel para algunas operaciones

import java.util.Optional;

public interface InventarioService {


    Optional<InventarioModel> obtenerInventarioPorProductoId(Long productoId);


    int obtenerStockDisponible(Long productoId);


    InventarioModel asegurarInventarioParaProducto(ProductoModel producto, int stockInicial);


    InventarioModel incrementarStock(Long productoId, int cantidadAIncrementar);


    InventarioModel disminuirStock(Long productoId, int cantidadADisminuir);


}