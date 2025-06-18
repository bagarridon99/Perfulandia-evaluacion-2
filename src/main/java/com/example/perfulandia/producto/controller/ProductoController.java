package com.example.perfulandia.producto.controller;

import com.example.perfulandia.model.ProductoModel; // Importa tu entidad
import com.example.perfulandia.producto.service.ProductoService; // Importa tu interfaz de servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Importaciones para las anotaciones de Spring MVC

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;


    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoModel> anadirProducto(@RequestBody ProductoModel producto) {
        ProductoModel nuevoProducto = productoService.anadirProducto(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoModel> buscarProductoPorId(@PathVariable Long id) {
        Optional<ProductoModel> productoOptional = productoService.buscarProductoPorId(id);
        if (productoOptional.isPresent()) {
            return new ResponseEntity<>(productoOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductoModel>> buscarTodosLosProductos() {
        List<ProductoModel> productos = productoService.buscarTodosLosProductos();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductoModel> editarProducto(@PathVariable Long id, @RequestBody ProductoModel productoConNuevosDatos) {
        try {
            ProductoModel productoActualizado = productoService.editarProducto(id, productoConNuevosDatos);
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}