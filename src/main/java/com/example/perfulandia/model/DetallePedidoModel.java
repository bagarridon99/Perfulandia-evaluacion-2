package com.example.perfulandia.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema; // Importar

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_pedido")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Representa un ítem individual dentro de un pedido, detallando un producto y su cantidad.")
public class DetallePedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del detalle de pedido.", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonBackReference
    @Schema(description = "El pedido al que pertenece este detalle.")
    private PedidosModel pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @Schema(description = "El producto incluido en este detalle de pedido.")
    private ProductoModel producto;

    @Column(nullable = false)
    @Schema(description = "Cantidad del producto en este detalle de pedido.", example = "2")
    private Integer cantidad;

    @Column(name = "precio_unitario_compra", nullable = false, precision = 10, scale = 2)
    @Schema(description = "El precio unitario del producto en el momento de la compra.", example = "75.00")
    private BigDecimal precioUnitarioAlMomentoDeCompra;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Subtotal de este detalle de pedido (cantidad * precio unitario al momento de compra).", example = "150.00")
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