package com.example.perfulandia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <-- IMPORTACIÓN AÑADIDA
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "productos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // <-- ANOTACIÓN AÑADIDA
public class ProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 100)
    private String marca;

    @Column(length = 100)
    private String categoria;

    @Column(name = "precio_unitario", nullable = false)
    private double precio;

    @Column(name = "tamanio_ml")
    private int tamanioMl;

    @OneToOne(
            mappedBy = "producto",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference("producto-inventario")
    private InventarioModel inventario;

    public ProductoModel() {
    }

    public ProductoModel(String nombre, String descripcion, String marca, String categoria, double precio, int tamanioMl) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.categoria = categoria;
        this.precio = precio;
        this.tamanioMl = tamanioMl;
    }

    // --- Getters y Setters (sin cambios) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getTamanioMl() { return tamanioMl; }
    public void setTamanioMl(int tamanioMl) { this.tamanioMl = tamanioMl; }
    public InventarioModel getInventario() { return inventario; }
    public void setInventario(InventarioModel inventario) {
        this.inventario = inventario;
        if (inventario != null && inventario.getProducto() != this) {
            inventario.setProducto(this);
        }
    }

    @Override
    public String toString() {
        return "ProductoModel{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", tamanioMl=" + tamanioMl +
                '}';
    }
}