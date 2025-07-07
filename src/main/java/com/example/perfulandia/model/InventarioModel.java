package com.example.perfulandia.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema; // Importar

@Entity
@Table(name = "inventarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Representa el registro de inventario para un producto específico.")
public class InventarioModel {

    @Id
    @Schema(description = "ID del inventario, que es el mismo ID que el producto asociado (relación OneToOne con MapsId).", example = "101")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "producto_id")
    @JsonBackReference("producto-inventario")
    @Schema(description = "El producto al que pertenece este registro de inventario.")
    private ProductoModel producto;

    @Column(nullable = false)
    @Schema(description = "Cantidad de unidades disponibles de este producto en el inventario.", example = "50")
    private Integer cantidadDisponible;

    public InventarioModel() {
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductoModel getProducto() { return producto; }
    public void setProducto(ProductoModel producto) { this.producto = producto; }
    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
}