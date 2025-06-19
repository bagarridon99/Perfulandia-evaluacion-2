package com.example.perfulandia.producto.controller;

import com.example.perfulandia.model.ProductoModel;
import com.example.perfulandia.producto.service.ProductoService;

// Importaciones de Spring HATEOAS
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link; // Puedes usar esta para Links manuales, aunque WebMvcLinkBuilder es mejor

// Importación estática para WebMvcLinkBuilder para un código más limpio
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// Importaciones de Swagger/OpenAPI (ya presentes)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody; // Importación para @RequestBody de Swagger

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Necesario para .stream().collect()

@RestController
@RequestMapping("/api/productos") // Ruta base para el controlador
@Tag(name = "Productos", description = "Operaciones de gestión de productos en Perfulandia")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista de todos los productos disponibles en el catálogo.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida con éxito.",
            // CAMBIO: La respuesta ahora es CollectionModel de EntityModel<ProductoModel>
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CollectionModel.class))) // Apunta al CollectionModel
    public ResponseEntity<CollectionModel<EntityModel<ProductoModel>>> getAllProductos() {
        List<ProductoModel> productos = productoService.buscarTodosLosProductos();

        // Convertir cada ProductoModel en un EntityModel y añadirle enlaces individuales
        List<EntityModel<ProductoModel>> resources = productos.stream()
                .map(producto -> {
                    EntityModel<ProductoModel> resource = EntityModel.of(producto); // Envuelve el producto
                    // Añadir enlace "self" para cada producto individual
                    resource.add(linkTo(methodOn(ProductoController.class).getProductoById(producto.getId())).withSelfRel());
                    // Puedes añadir otros enlaces para acciones específicas de CADA producto (ej. editar, eliminar)
                    resource.add(linkTo(methodOn(ProductoController.class).updateProducto(producto.getId(), null)).withRel("update"));
                    resource.add(linkTo(methodOn(ProductoController.class).deleteProducto(producto.getId())).withRel("delete"));
                    return resource;
                })
                .collect(Collectors.toList());

        // 1. Envolver la lista de EntityModels en un CollectionModel
        CollectionModel<EntityModel<ProductoModel>> collectionModel = CollectionModel.of(resources);

        // 2. Añadir un enlace "self" a la colección completa de productos
        collectionModel.add(linkTo(methodOn(ProductoController.class).getAllProductos()).withSelfRel());

        // 3. Añadir un enlace para la acción de crear un nuevo producto (ej. POST /api/productos)
        collectionModel.add(linkTo(methodOn(ProductoController.class).createProducto(null)).withRel("create"));

        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Busca y devuelve un producto específico por su ID único.")
    @ApiResponse(responseCode = "200", description = "Producto encontrado y devuelto con éxito.",
            // CAMBIO: La respuesta ahora es EntityModel<ProductoModel>
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EntityModel.class))) // Apunta al EntityModel
    @ApiResponse(responseCode = "404", description = "Producto no encontrado con el ID proporcionado.")
    public ResponseEntity<EntityModel<ProductoModel>> getProductoById( // CAMBIO: Tipo de retorno
                                                                       @Parameter(description = "ID del producto a buscar.", required = true, example = "1")
                                                                       @PathVariable Long id) {
        Optional<ProductoModel> productoOptional = productoService.buscarProductoPorId(id);

        return productoOptional.map(producto -> {
            EntityModel<ProductoModel> resource = EntityModel.of(producto); // Envuelve el producto

            // Enlace "self" para este recurso
            resource.add(linkTo(methodOn(ProductoController.class).getProductoById(id)).withSelfRel());

            // Enlace a la colección principal (todos los productos)
            resource.add(linkTo(methodOn(ProductoController.class).getAllProductos()).withRel("all-products"));

            // Enlace a la acción de actualizar
            resource.add(linkTo(methodOn(ProductoController.class).updateProducto(id, null)).withRel("update"));

            // Enlace a la acción de eliminar
            resource.add(linkTo(methodOn(ProductoController.class).deleteProducto(id)).withRel("delete"));

            return new ResponseEntity<>(resource, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo producto", description = "Añade un nuevo producto al catálogo de Perfulandia.")
    @ApiResponse(responseCode = "201", description = "Producto creado con éxito.",
            // CAMBIO: La respuesta ahora es EntityModel<ProductoModel>
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EntityModel.class))) // Apunta al EntityModel
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos para el producto.")
    public ResponseEntity<EntityModel<ProductoModel>> createProducto( // CAMBIO: Tipo de retorno
                                                                      @RequestBody(description = "Objeto Producto a crear.", required = true,
                                                                              content = @Content(schema = @Schema(implementation = ProductoModel.class)))
                                                                      @org.springframework.web.bind.annotation.RequestBody ProductoModel producto) { // NOTA: Aquí usé el @RequestBody de Spring
        ProductoModel createdProducto = productoService.anadirProducto(producto);

        EntityModel<ProductoModel> resource = EntityModel.of(createdProducto);

        // Enlace "self" para el recurso recién creado (usando su ID)
        resource.add(linkTo(methodOn(ProductoController.class).getProductoById(createdProducto.getId())).withSelfRel());

        // Enlace a la colección de todos los productos
        resource.add(linkTo(methodOn(ProductoController.class).getAllProductos()).withRel("all-products"));

        // Retorna 201 Created y la URI del nuevo recurso, obtenida del enlace "self"
        return ResponseEntity.created(resource.getRequiredLink("self").toUri()).body(resource);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente", description = "Actualiza los detalles de un producto por su ID.")
    @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito.",
            // CAMBIO: La respuesta ahora es EntityModel<ProductoModel>
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EntityModel.class))) // Apunta al EntityModel
    @ApiResponse(responseCode = "404", description = "Producto no encontrado.")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos para el producto.")
    public ResponseEntity<EntityModel<ProductoModel>> updateProducto( // CAMBIO: Tipo de retorno
                                                                      @Parameter(description = "ID del producto a actualizar.", required = true, example = "1") @PathVariable Long id,
                                                                      @RequestBody(description = "Objeto Producto con los nuevos datos.", required = true,
                                                                              content = @Content(schema = @Schema(implementation = ProductoModel.class)))
                                                                      @org.springframework.web.bind.annotation.RequestBody ProductoModel productoDetails) { // NOTA: Aquí usé el @RequestBody de Spring
        ProductoModel updatedProducto = productoService.editarProducto(id, productoDetails);
        // Suponiendo que editarProducto devuelve el producto actualizado o lanza excepción si no existe
        // Si puede devolver null si no lo encuentra, necesitarías un manejo similar al GET
        if (updatedProducto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        EntityModel<ProductoModel> resource = EntityModel.of(updatedProducto);

        // Enlace "self" para el recurso actualizado
        resource.add(linkTo(methodOn(ProductoController.class).getProductoById(updatedProducto.getId())).withSelfRel());

        // Enlace a la colección de todos los productos
        resource.add(linkTo(methodOn(ProductoController.class).getAllProductos()).withRel("all-products"));

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto del catálogo por su ID.")
    @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito (No Content).")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado.")
    public ResponseEntity<Void> deleteProducto(
            @Parameter(description = "ID del producto a eliminar.", required = true, example = "1")
            @PathVariable Long id) {
        productoService.eliminarProducto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}