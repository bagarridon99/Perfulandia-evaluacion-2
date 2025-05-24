package com.example.perfulandia.pedidos.service;

import com.example.perfulandia.model.PedidosModel;
import com.example.perfulandia.model.enums.EstadoPedido;
import com.example.perfulandia.pedidos.dto.CrearPedidoRequestDTO;

import java.util.List;
import java.util.Optional;

public interface PedidosService {

    PedidosModel crearPedido(CrearPedidoRequestDTO requestDTO, String emailUsuarioAutenticado);

    Optional<PedidosModel> obtenerPedidoPorIdYUsuario(Long pedidoId, String emailUsuarioAutenticado);

    List<PedidosModel> obtenerPedidosPorUsuario(String emailUsuarioAutenticado);

    PedidosModel actualizarEstadoPedidoAdmin(Long pedidoId, EstadoPedido nuevoEstado);
}