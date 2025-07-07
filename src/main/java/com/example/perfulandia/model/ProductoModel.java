package com.example.perfulandia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "productos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Detalles completos de un producto de perfumería en Perfulandia.")
public class ProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del producto.", example = "101")
    private Long id;

    @Column(nullable = false, length = 200)
    @Schema(description = "Nombre del producto de perfumería.", example = "Perfume Floral Primavera")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción detallada del perfume, incluyendo notas y características.", example = "Fragancia ligera con notas de jazmín y rosa, ideal para el día a día.")
    private String descripcion;

    @Column(length = 100)
    @Schema(description = "Marca del producto.", example = "EssenceLux")
    private String marca;

    @Column(length = 100)
    @Schema(description = "Categoría a la que pertenece el producto (ej. 'Perfumes Mujer', 'Cuidado Corporal').", example = "Perfumes Mujer")
    private String categoria;

    @Column(name = "precio_unitario", nullable = false)
    @Schema(description = "Precio de venta unitario del producto.", example = "85.50")
    private double precio;

    @Column(name = "tamanio_ml")
    @Schema(description = "Tamaño del producto en mililitros (ml).", example = "75")
    private int tamanioMl;

    @OneToOne(
            mappedBy = "producto",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference("producto-inventario")
    @Schema(description = "Información de inventario asociada a este producto.")
    private InventarioModel inventario;

    public ProductoModel() {
    }

    // Tu constructor existente sin ID
    public ProductoModel(String nombre, String descripcion, String marca, String categoria, double precio, int tamanioMl) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.categoria = categoria;
        this.precio = precio;
        this.tamanioMl = tamanioMl;
    }

    // ¡NUEVO CONSTRUCTOR QUE EL TEST NECESITA (con ID)!
    // Este constructor permite crear ProductoModel con todos los campos, incluido el ID.
    public ProductoModel(Long id, String nombre, String descripcion, String marca, String categoria, double precio, int tamanioMl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.categoria = categoria;
        this.precio = precio;
        this.tamanioMl = tamanioMl;
        // inventario no se incluye aquí para simplificar el constructor para tests,
        // ya que es una relación OneToOne y se puede setear por separado.
    }


    // --- GETTERS Y SETTERS --- (Asegúrate de tenerlos todos, como ya los tienes)
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