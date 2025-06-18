package com.example.perfulandia.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <-- IMPORTACIÓN AÑADIDA
import jakarta.persistence.*;

@Entity
@Table(name = "inventarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InventarioModel {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "producto_id")
    @JsonBackReference("producto-inventario")
    private ProductoModel producto;

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