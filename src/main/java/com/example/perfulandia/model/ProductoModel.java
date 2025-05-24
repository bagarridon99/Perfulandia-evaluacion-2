package com.example.perfulandia.model; // Paquete donde reside la entidad

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne; // Nueva importación para la relación
import jakarta.persistence.CascadeType;  // Nueva importación para la cascada

// Asume que InventarioModel está en el mismo paquete o importa la ruta correcta
// Ejemplo: import com.example.perfulandia.model.InventarioModel;
// O si está en el paquete de inventario: import com.example.perfulandia.inventario.model.InventarioModel;

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

    // --- INICIO DE CAMBIOS: Relación con InventarioModel ---
    @OneToOne(
            mappedBy = "producto",             // Indica que la gestión de la FK está en el campo "producto" de InventarioModel
            cascade = CascadeType.ALL,         // Las operaciones (guardar, borrar, etc.) en Producto se propagan a Inventario
            orphanRemoval = true               // Si se quita un inventario de un producto, se borra de la BD
    )
    private InventarioModel inventario;
    // --- FIN DE CAMBIOS: Relación con InventarioModel ---

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
    // (Tus getters y setters existentes para id, nombre, descripcion, etc. permanecen igual)

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

    // --- INICIO DE CAMBIOS: Getters y Setters para InventarioModel ---
    public InventarioModel getInventario() {
        return inventario;
    }

    public void setInventario(InventarioModel inventario) {
        this.inventario = inventario;
        // Para mantener la consistencia en una relación bidireccional,
        // también establecemos este producto en el inventario.
        if (inventario != null && inventario.getProducto() != this) {
            inventario.setProducto(this);
        }
    }
    // --- FIN DE CAMBIOS: Getters y Setters para InventarioModel ---

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
                // Opcional: ", inventario=" + (inventario != null ? inventario.getId() : "null") + // Evita recursión si InventarioModel.toString() llama a ProductoModel.toString()
                '}';
    }
}