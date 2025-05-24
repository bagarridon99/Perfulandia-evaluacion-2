package com.example.perfulandia.pedidos.repository;

import com.example.perfulandia.model.PedidosModel;
import com.example.perfulandia.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidosRepository extends JpaRepository<PedidosModel, Long> {

    List<PedidosModel> findByUsuarioOrderByFechaPedidoDesc(UsuarioModel usuario);

    Optional<PedidosModel> findByIdAndUsuario(Long id, UsuarioModel usuario);
}