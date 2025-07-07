package com.example.perfulandia.inventario.controller;

import com.example.perfulandia.model.InventarioModel;
import com.example.perfulandia.model.ProductoModel; // <-- IMPORTA ProductoModel
import com.example.perfulandia.inventario.service.InventarioService;
import com.example.perfulandia.producto.ProductoService; // <-- IMPORTA ProductoService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventarios")
public class InventarioController {

    private final InventarioService inventarioService;
    private final ProductoService productoService;

    @Autowired
    public InventarioController(InventarioService inventarioService, ProductoService productoService) {
        this.inventarioService = inventarioService;
        this.productoService = productoService;
    }

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


    @PostMapping("/producto/{productoId}/asegurar")
    public ResponseEntity<?> asegurarInventario(
            @PathVariable Long productoId,
            @RequestParam(defaultValue = "0") int stockInicial) {
        try {

            Optional<ProductoModel> productoOpt = productoService.buscarProductoPorId(productoId);
            if (productoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado con ID: " + productoId);
            }
            ProductoModel producto = productoOpt.get();


            InventarioModel inventario = inventarioService.asegurarInventarioParaProducto(producto, stockInicial);
            return new ResponseEntity<>(inventario, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al asegurar el inventario: " + e.getMessage());
        }
    }

}