package com.example.perfulandia.producto.controller;

import com.example.perfulandia.model.ProductoModel;
import com.example.perfulandia.producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Operaciones de gestión de productos en Perfulandia")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista de todos los productos disponibles en el catálogo.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida con éxito.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoModel.class)))
    public ResponseEntity<List<ProductoModel>> getAllProductos() {
        // CAMBIO AQUÍ: Llamar al método correcto del servicio
        List<ProductoModel> productos = productoService.buscarTodosLosProductos();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Busca y devuelve un producto específico por su ID único.")
    @ApiResponse(responseCode = "200", description = "Producto encontrado y devuelto con éxito.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoModel.class)))
    @ApiResponse(responseCode = "404", description = "Producto no encontrado con el ID proporcionado.")
    public ResponseEntity<ProductoModel> getProductoById(
            @Parameter(description = "ID del producto a buscar.", required = true, example = "1")
            @PathVariable Long id) {
        // CAMBIO AQUÍ: Llamar al método correcto del servicio
        Optional<ProductoModel> producto = productoService.buscarProductoPorId(id);
        return producto.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Añade un nuevo producto al catálogo de Perfulandia.")
    @ApiResponse(responseCode = "201", description = "Producto creado con éxito.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoModel.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos para el producto.")
    public ResponseEntity<ProductoModel> createProducto(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Producto a crear.", required = true,
                    content = @Content(schema = @Schema(implementation = ProductoModel.class)))
            @RequestBody ProductoModel producto) {
        // CAMBIO AQUÍ: Llamar al método correcto del servicio
        ProductoModel createdProducto = productoService.anadirProducto(producto);
        return new ResponseEntity<>(createdProducto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente", description = "Actualiza los detalles de un producto por su ID.")
    @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductoModel.class)))
    @ApiResponse(responseCode = "404", description = "Producto no encontrado.")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos para el producto.")
    public ResponseEntity<ProductoModel> updateProducto(
            @Parameter(description = "ID del producto a actualizar.", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Producto con los nuevos datos.", required = true,
                    content = @Content(schema = @Schema(implementation = ProductoModel.class)))
            @RequestBody ProductoModel productoDetails) {
        // CAMBIO AQUÍ: Llamar al método correcto del servicio
        ProductoModel updatedProducto = productoService.editarProducto(id, productoDetails);
        return new ResponseEntity<>(updatedProducto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto del catálogo por su ID.")
    @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito (No Content).")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado.")
    public ResponseEntity<Void> deleteProducto(
            @Parameter(description = "ID del producto a eliminar.", required = true, example = "1")
            @PathVariable Long id) {
        // CAMBIO AQUÍ: Llamar al método correcto del servicio
        productoService.eliminarProducto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}