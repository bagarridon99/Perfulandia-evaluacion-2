package com.example.perfulandia.model; // Paquete donde reside la entidad

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "productos") // Nombre de la tabla en la base de datos
public class ProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // El ID se autogenerará por la BD
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT") // Para descripciones largas
    private String descripcion;

    @Column(length = 100)
    private String marca;

    @Column(length = 100)
    private String categoria; // Ej: "Floral", "Amaderado", "Oriental"

    @Column(name = "precio_unitario", nullable = false)
    private double precio; // Considera BigDecimal para mayor precisión si es necesario

    @Column(name = "tamanio_ml")
    private int tamanioMl; // Tamaño en mililitros

    // --- Constructores ---

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

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getTamanioMl() {
        return tamanioMl;
    }

    public void setTamanioMl(int tamanioMl) {
        this.tamanioMl = tamanioMl;
    }

    // (Opcional) Método toString para facilitar la depuración
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