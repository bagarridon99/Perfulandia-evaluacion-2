package com.example.perfulandia.pedidos.service;

import com.example.perfulandia.inventario.service.InventarioService;
import com.example.perfulandia.model.DetallePedidoModel;
import com.example.perfulandia.model.PedidosModel;
import com.example.perfulandia.model.ProductoModel;
import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.model.enums.EstadoPedido;
import com.example.perfulandia.pedidos.dto.CrearPedidoRequestDTO;
import com.example.perfulandia.pedidos.dto.ItemPedidoDTO;
import com.example.perfulandia.pedidos.repository.PedidosRepository;
import com.example.perfulandia.producto.repository.ProductoRepository;
import com.example.perfulandia.usuario.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidosServiceImpl implements PedidosService {

    private final PedidosRepository pedidosRepository;
    private final UsuarioService usuarioService;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    @Autowired
    public PedidosServiceImpl(PedidosRepository pedidosRepository,
                              UsuarioService usuarioService,
                              ProductoRepository productoRepository,
                              InventarioService inventarioService) {
        this.pedidosRepository = pedidosRepository;
        this.usuarioService = usuarioService;
        this.productoRepository = productoRepository;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional
    public PedidosModel crearPedido(CrearPedidoRequestDTO requestDTO, String emailUsuarioAutenticado) {
        UsuarioModel usuario = usuarioService.buscarPorEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + emailUsuarioAutenticado));

        if (requestDTO.items() == null || requestDTO.items().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un ítem.");
        }

        PedidosModel nuevoPedido = new PedidosModel();
        nuevoPedido.setUsuario(usuario);
        // fechaPedido y estado se inicializan en el constructor de PedidosModel

        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDTO : requestDTO.items()) {
            if (itemDTO.cantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad para el producto ID " + itemDTO.productoId() + " debe ser positiva.");
            }

            ProductoModel producto = productoRepository.findById(itemDTO.productoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + itemDTO.productoId()));

            int stockDisponible = inventarioService.obtenerStockDisponible(producto.getId());
            if (stockDisponible < itemDTO.cantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() +
                        ". Stock actual: " + stockDisponible + ", Solicitado: " + itemDTO.cantidad());
            }

            DetallePedidoModel detalle = new DetallePedidoModel();
            detalle.setProducto(producto);
            detalle.setCantidad(itemDTO.cantidad());
            detalle.setPrecioUnitarioAlMomentoDeCompra(BigDecimal.valueOf(producto.getPrecio()));
            BigDecimal subtotalItem = detalle.getPrecioUnitarioAlMomentoDeCompra().multiply(BigDecimal.valueOf(itemDTO.cantidad()));
            detalle.setSubtotal(subtotalItem);

            nuevoPedido.agregarDetalle(detalle);
            totalGeneral = totalGeneral.add(subtotalItem);

            inventarioService.disminuirStock(producto.getId(), itemDTO.cantidad());
        }

        nuevoPedido.setTotalPedido(totalGeneral);
        return pedidosRepository.save(nuevoPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PedidosModel> obtenerPedidoPorIdYUsuario(Long pedidoId, String emailUsuarioAutenticado) {
        UsuarioModel usuario = usuarioService.buscarPorEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado o no encontrado al buscar pedido."));
        return pedidosRepository.findByIdAndUsuario(pedidoId, usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidosModel> obtenerPedidosPorUsuario(String emailUsuarioAutenticado) {
        UsuarioModel usuario = usuarioService.buscarPorEmail(emailUsuarioAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para listar pedidos."));
        return pedidosRepository.findByUsuarioOrderByFechaPedidoDesc(usuario);
    }

    @Override
    @Transactional
    public PedidosModel actualizarEstadoPedidoAdmin(Long pedidoId, EstadoPedido nuevoEstado) {
        PedidosModel pedido = pedidosRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        pedido.setEstado(nuevoEstado);
        return pedidosRepository.save(pedido);
    }
}