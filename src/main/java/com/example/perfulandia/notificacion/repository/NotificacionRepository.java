package com.example.perfulandia.notificacion.repository;

import com.example.perfulandia.model.NotificacionModel;
import com.example.perfulandia.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionModel, Long> {

    List<NotificacionModel> findByUsuarioDestinatarioOrderByFechaCreacionDesc(UsuarioModel usuario);

    List<NotificacionModel> findByUsuarioDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(UsuarioModel usuario);

    // Para contar notificaciones no leídas
    long countByUsuarioDestinatarioAndLeidaFalse(UsuarioModel usuario);
}
