package com.example.perfulandia.model; // O el paquete donde tengas InventarioModel

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;

@Entity
@Table(name = "inventarios")
public class InventarioModel {

    @Id
    private Long id; // O @GeneratedValue si es un ID independiente

    @OneToOne // Este es el lado dueño
    @MapsId // Opcional: si el ID de inventario es el mismo que el producto_id. Si es así, el campo 'id' de arriba se mapeará a producto_id.
    @JoinColumn(name = "producto_id") // Define la columna de llave foránea en la tabla 'inventarios'
    private ProductoModel producto; // Asegúrate que ProductoModel esté accesible (importado si es necesario)

    private Integer cantidadDisponible;

    // Faltarían los constructores, getters y setters aquí si aún no los has añadido:
    public InventarioModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductoModel getProducto() {
        return producto;
    }

    public void setProducto(ProductoModel producto) {
        this.producto = producto;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    // ... más campos, getters, setters, constructores si los necesitas ...
}