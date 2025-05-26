package com.example.perfulandia.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <-- IMPORTACIÓN AÑADIDA
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_pedido")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DetallePedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonBackReference
    private PedidosModel pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoModel producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioAlMomentoDeCompra;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public DetallePedidoModel() {}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PedidosModel getPedido() { return pedido; }
    public void setPedido(PedidosModel pedido) { this.pedido = pedido; }
    public ProductoModel getProducto() { return producto; }
    public void setProducto(ProductoModel producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitarioAlMomentoDeCompra() { return precioUnitarioAlMomentoDeCompra; }
    public void setPrecioUnitarioAlMomentoDeCompra(BigDecimal precioUnitarioAlMomentoDeCompra) { this.precioUnitarioAlMomentoDeCompra = precioUnitarioAlMomentoDeCompra; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}