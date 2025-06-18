package com.example.perfulandia.notificacion.service;

import com.example.perfulandia.model.NotificacionModel;
import com.example.perfulandia.model.UsuarioModel;

import java.util.List;
import java.util.Optional; // <-- ¡AÑADE ESTA LÍNEA!

public interface NotificacionService {

    NotificacionModel crearNotificacion(UsuarioModel destinatario, String mensaje);

    List<NotificacionModel> obtenerNotificacionesPorUsuario(String emailUsuario);

    List<NotificacionModel> obtenerNotificacionesNoLeidasPorUsuario(String emailUsuario);

    long contarNotificacionesNoLeidasPorUsuario(String emailUsuario);

    Optional<NotificacionModel> marcarNotificacionComoLeida(Long notificacionId, String emailUsuario);

    int marcarTodasLasNotificacionesComoLeidas(String emailUsuario);

    void eliminarNotificacion(Long notificacionId, String emailUsuario);
}