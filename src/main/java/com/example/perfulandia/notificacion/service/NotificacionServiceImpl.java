package com.example.perfulandia.notificacion.service;

import com.example.perfulandia.model.NotificacionModel;
import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.notificacion.repository.NotificacionRepository;
import com.example.perfulandia.usuario.service.UsuarioService; // Para obtener el UsuarioModel

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public NotificacionServiceImpl(NotificacionRepository notificacionRepository, UsuarioService usuarioService) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    @Transactional
    public NotificacionModel crearNotificacion(UsuarioModel destinatario, String mensaje) {
        if (destinatario == null) {
            throw new IllegalArgumentException("El destinatario no puede ser nulo para crear una notificación.");
        }
        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje de la notificación no puede estar vacío.");
        }

        NotificacionModel notificacion = new NotificacionModel();
        notificacion.setUsuarioDestinatario(destinatario);
        notificacion.setMensaje(mensaje);

        return notificacionRepository.save(notificacion);
    }

    private UsuarioModel obtenerUsuarioPorEmail(String emailUsuario) {
        return usuarioService.buscarPorEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + emailUsuario));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionModel> obtenerNotificacionesPorUsuario(String emailUsuario) {
        UsuarioModel usuario = obtenerUsuarioPorEmail(emailUsuario);
        return notificacionRepository.findByUsuarioDestinatarioOrderByFechaCreacionDesc(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionModel> obtenerNotificacionesNoLeidasPorUsuario(String emailUsuario) {
        UsuarioModel usuario = obtenerUsuarioPorEmail(emailUsuario);
        return notificacionRepository.findByUsuarioDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNotificacionesNoLeidasPorUsuario(String emailUsuario) {
        UsuarioModel usuario = obtenerUsuarioPorEmail(emailUsuario);
        return notificacionRepository.countByUsuarioDestinatarioAndLeidaFalse(usuario);
    }

    @Override
    @Transactional
    public Optional<NotificacionModel> marcarNotificacionComoLeida(Long notificacionId, String emailUsuario) {
        UsuarioModel usuario = obtenerUsuarioPorEmail(emailUsuario);
        Optional<NotificacionModel> notificacionOpt = notificacionRepository.findById(notificacionId);

        if (notificacionOpt.isPresent()) {
            NotificacionModel notificacion = notificacionOpt.get();
            if (!notificacion.getUsuarioDestinatario().getId().equals(usuario.getId())) {

                throw new SecurityException("El usuario no tiene permiso para modificar esta notificación.");

            }
            if (!notificacion.isLeida()) {
                notificacion.setLeida(true);
                notificacionRepository.save(notificacion);
            }
            return Optional.of(notificacion);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public int marcarTodasLasNotificacionesComoLeidas(String emailUsuario) {
        UsuarioModel usuario = obtenerUsuarioPorEmail(emailUsuario);
        List<NotificacionModel> noLeidas = notificacionRepository.findByUsuarioDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(usuario);

        if (noLeidas.isEmpty()) {
            return 0;
        }

        for (NotificacionModel notificacion : noLeidas) {
            notificacion.setLeida(true);
        }
        notificacionRepository.saveAll(noLeidas);
        return noLeidas.size();
    }

    @Override
    @Transactional
    public void eliminarNotificacion(Long notificacionId, String emailUsuario) {
        UsuarioModel usuario = obtenerUsuarioPorEmail(emailUsuario);
        NotificacionModel notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + notificacionId));

        if (!notificacion.getUsuarioDestinatario().getId().equals(usuario.getId())) {
            throw new SecurityException("El usuario no tiene permiso para eliminar esta notificación.");
        }
        notificacionRepository.deleteById(notificacionId);
    }
}
