package com.example.perfulandia.producto.controller;

import com.example.perfulandia.model.ProductoModel; // Importa tu entidad
import com.example.perfulandia.producto.service.ProductoService; // Importa tu interfaz de servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Importaciones para las anotaciones de Spring MVC

import java.util.List;
import java.util.Optional;

@RestController // Indica que esta clase es un controlador REST y que los métodos devolverán datos directamente en el cuerpo de la respuesta (ej. JSON)
@RequestMapping("/api/v1/productos") // Define la ruta base para todos los endpoints en este controlador
public class ProductoController {

    private final ProductoService productoService;


    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoModel> anadirProducto(@RequestBody ProductoModel producto) {
        // @RequestBody indica que el cuerpo de la petición HTTP (ej. un JSON) se convertirá a un objeto ProductoModel
        ProductoModel nuevoProducto = productoService.anadirProducto(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED); // Devuelve el producto creado y el estado HTTP 201 (Created)
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoModel> buscarProductoPorId(@PathVariable Long id) {
        // @PathVariable indica que el valor de "id" en la URL se pasará como parámetro al método
        Optional<ProductoModel> productoOptional = productoService.buscarProductoPorId(id);
        if (productoOptional.isPresent()) {
            return new ResponseEntity<>(productoOptional.get(), HttpStatus.OK); // Devuelve el producto y el estado HTTP 200 (OK)
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Devuelve el estado HTTP 404 (Not Found) si no existe
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductoModel>> buscarTodosLosProductos() {
        List<ProductoModel> productos = productoService.buscarTodosLosProductos();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    // Endpoint para EDITAR (actualizar) un producto existente
    // HTTP PUT a /api/v1/productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductoModel> editarProducto(@PathVariable Long id, @RequestBody ProductoModel productoConNuevosDatos) {
        try {
            ProductoModel productoActualizado = productoService.editarProducto(id, productoConNuevosDatos);
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        } catch (RuntimeException e) { // Captura la excepción si el producto no se encuentra (lanzada por el servicio)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Devuelve el estado HTTP 204 (No Content) indicando éxito sin cuerpo de respuesta
        } catch (RuntimeException e) { // Captura la excepción si el producto no se encuentra
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}