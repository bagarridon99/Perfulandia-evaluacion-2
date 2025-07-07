package com.example.perfulandia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema; // Importar

@Entity
@Table(name = "notificaciones")
@Schema(description = "Representa una notificación enviada a un usuario.")
public class NotificacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la notificación.", example = "1")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "El contenido del mensaje de la notificación.", example = "Tu pedido #1001 ha sido enviado.")
    private String mensaje;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora en que la notificación fue creada.", example = "2024-06-18T11:00:00")
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    @Schema(description = "Indica si la notificación ha sido leída por el usuario (true/false).", example = "false")
    private boolean leida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @Schema(description = "El usuario que es el destinatario de esta notificación.")
    private UsuarioModel usuarioDestinatario;

    public NotificacionModel() {
        this.fechaCreacion = LocalDateTime.now();
        this.leida = false;
    }

    public NotificacionModel(String mensaje, UsuarioModel usuarioDestinatario) {
        this();
        this.mensaje = mensaje;
        this.usuarioDestinatario = usuarioDestinatario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
    public UsuarioModel getUsuarioDestinatario() { return usuarioDestinatario; }
    public void setUsuarioDestinatario(UsuarioModel usuarioDestinatario) { this.usuarioDestinatario = usuarioDestinatario; }
}