package com.example.perfulandia.inventario.controller;

import com.example.perfulandia.model.InventarioModel;
import com.example.perfulandia.model.ProductoModel; // <-- IMPORTA ProductoModel
import com.example.perfulandia.inventario.service.InventarioService;
import com.example.perfulandia.producto.service.ProductoService; // <-- IMPORTA ProductoService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventarios") // Ruta base para los endpoints de inventario
public class InventarioController {

    private final InventarioService inventarioService;
    private final ProductoService productoService; // <-- INYECTA ProductoService

    @Autowired
    public InventarioController(InventarioService inventarioService, ProductoService productoService) { // <-- AÑADE ProductoService AL CONSTRUCTOR
        this.inventarioService = inventarioService;
        this.productoService = productoService; // <-- ASIGNA ProductoService
    }

    // Endpoint para obtener el registro de inventario completo de un producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> obtenerInventarioPorProductoId(@PathVariable Long productoId) {
        Optional<InventarioModel> inventarioOpt = inventarioService.obtenerInventarioPorProductoId(productoId);
        if (inventarioOpt.isPresent()) {
            return ResponseEntity.ok(inventarioOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró inventario para el producto ID: " + productoId);
        }
    }

    // Endpoint para obtener solo la cantidad de stock disponible de un producto
    @GetMapping("/producto/{productoId}/stock")
    public ResponseEntity<?> obtenerStockDisponible(@PathVariable Long productoId) {
        try {
            int stock = inventarioService.obtenerStockDisponible(productoId);
            return ResponseEntity.ok().body("{\"productoId\": " + productoId + ", \"stockDisponible\": " + stock + "}");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró inventario o producto con ID: " + productoId);
        }
    }

    // Endpoint para incrementar el stock de un producto
    @PutMapping("/producto/{productoId}/incrementar")
    public ResponseEntity<?> incrementarStock(@PathVariable Long productoId, @RequestParam int cantidad) {
        try {
            InventarioModel inventarioActualizado = inventarioService.incrementarStock(productoId, cantidad);
            return ResponseEntity.ok(inventarioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Endpoint para disminuir el stock de un producto
    @PutMapping("/producto/{productoId}/disminuir")
    public ResponseEntity<?> disminuirStock(@PathVariable Long productoId, @RequestParam int cantidad) {
        try {
            InventarioModel inventarioActualizado = inventarioService.disminuirStock(productoId, cantidad);
            return ResponseEntity.ok(inventarioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // --- MÉTODO NUEVO AÑADIDO ---
    // Endpoint para asegurar o crear el inventario para un producto existente
    @PostMapping("/producto/{productoId}/asegurar")
    public ResponseEntity<?> asegurarInventario(
            @PathVariable Long productoId,
            @RequestParam(defaultValue = "0") int stockInicial) { // stockInicial como parámetro de la URL
        try {
            // 1. Buscar el ProductoModel usando el productoId
            Optional<ProductoModel> productoOpt = productoService.buscarProductoPorId(productoId);
            if (productoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado con ID: " + productoId);
            }
            ProductoModel producto = productoOpt.get();

            // 2. Asegurar/crear el inventario para este producto
            InventarioModel inventario = inventarioService.asegurarInventarioParaProducto(producto, stockInicial);
            return new ResponseEntity<>(inventario, HttpStatus.CREATED); // Devuelve el inventario creado/actualizado

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) { // Otros errores posibles desde el servicio
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al asegurar el inventario: " + e.getMessage());
        }
    }

}