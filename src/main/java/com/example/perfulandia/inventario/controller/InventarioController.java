package com.example.perfulandia.inventario.controller;

import com.example.perfulandia.model.InventarioModel;
import com.example.perfulandia.inventario.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventarios") // Ruta base para los endpoints de inventario
public class InventarioController {

    private final InventarioService inventarioService;

    @Autowired
    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
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
            // Devolvemos un objeto JSON simple para la cantidad de stock
            // Podrías crear una pequeña clase DTO (Data Transfer Object) para esto si prefieres
            return ResponseEntity.ok().body("{\"productoId\": " + productoId + ", \"stockDisponible\": " + stock + "}");
        } catch (RuntimeException e) { // Por si el inventario no existe y obtenerStockDisponible lanza excepción
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró inventario o producto con ID: " + productoId);
        }
    }

    // Endpoint para incrementar el stock de un producto
    // Usamos PUT porque estamos actualizando un recurso existente (el stock del inventario)
    @PutMapping("/producto/{productoId}/incrementar")
    public ResponseEntity<?> incrementarStock(@PathVariable Long productoId, @RequestParam int cantidad) {
        try {
            InventarioModel inventarioActualizado = inventarioService.incrementarStock(productoId, cantidad);
            return ResponseEntity.ok(inventarioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) { // Para "No se encontró inventario..."
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
        } catch (RuntimeException e) { // Para "No se encontró inventario..." o "No hay stock suficiente..."
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict si no hay stock
        }
    }

    }
