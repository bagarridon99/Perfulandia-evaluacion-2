package com.example.perfulandia.model;

import com.example.perfulandia.model.enums.EstadoPedido;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema; // Importar

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Representa un pedido realizado por un usuario en Perfulandia.")
public class PedidosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del pedido.", example = "1001")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora en que se realizó el pedido.", example = "2024-06-18T10:30:00")
    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Schema(description = "Estado actual del pedido (PENDIENTE, PAGADO, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO, REEMBOLSADO).", example = "PENDIENTE")
    private EstadoPedido estado;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Monto total del pedido.", example = "150.75")
    private BigDecimal totalPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @Schema(description = "Usuario que realizó el pedido.")
    private UsuarioModel usuario;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    @Schema(description = "Lista de detalles que componen este pedido.")
    private List<DetallePedidoModel> detalles = new ArrayList<>();

    public PedidosModel() {
        this.fechaPedido = LocalDateTime.now();
        this.estado = EstadoPedido.PENDIENTE;
        this.totalPedido = BigDecimal.ZERO;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public BigDecimal getTotalPedido() { return totalPedido; }
    public void setTotalPedido(BigDecimal totalPedido) { this.totalPedido = totalPedido; }
    public UsuarioModel getUsuario() { return usuario; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }
    public List<DetallePedidoModel> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoModel> detalles) { this.detalles = detalles; }

    public void agregarDetalle(DetallePedidoModel detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
        detalle.setPedido(this);
    }

    public void removerDetalle(DetallePedidoModel detalle) {
        if (this.detalles != null) {
            this.detalles.remove(detalle);
            detalle.setPedido(null);
        }
    }
}