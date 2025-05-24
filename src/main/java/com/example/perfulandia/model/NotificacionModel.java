package com.example.perfulandia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class NotificacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean leida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
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

    // Getters y Setters para todos los campos...
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
