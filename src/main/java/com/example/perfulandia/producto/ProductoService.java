package com.example.perfulandia.producto;

import com.example.perfulandia.model.ProductoModel; // Importa tu entidad
import java.util.List;
import java.util.Optional;

public interface ProductoService {


    ProductoModel anadirProducto(ProductoModel producto);


    Optional<ProductoModel> buscarProductoPorId(Long id);


    List<ProductoModel> buscarTodosLosProductos();


    ProductoModel editarProducto(Long id, ProductoModel productoConNuevosDatos);


    void eliminarProducto(Long id);
}