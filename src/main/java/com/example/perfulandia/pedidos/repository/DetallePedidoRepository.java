package com.example.perfulandia.pedidos.repository;

import com.example.perfulandia.model.DetallePedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedidoModel, Long> {

}