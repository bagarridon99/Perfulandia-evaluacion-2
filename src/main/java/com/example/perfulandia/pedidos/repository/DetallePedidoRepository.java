package com.example.perfulandia.pedidos.repository;

import com.example.perfulandia.model.DetallePedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedidoModel, Long> {
    // Usualmente no se necesitan métodos personalizados aquí al inicio
    // si los detalles se manejan por cascada desde PedidosModel.
}