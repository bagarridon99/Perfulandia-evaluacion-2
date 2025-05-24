package com.example.perfulandia.pedidos.service;

import com.example.perfulandia.model.DetallePedidoModel;
import com.example.perfulandia.model.PedidosModel;
import com.example.perfulandia.model.ProductoModel;
import com.example.perfulandia.model.UsuarioModel;
import com.example.perfulandia.model.enums.EstadoPedido;
import com.example.perfulandia.pedidos.dto.CrearPedidoRequestDTO;
import com.example.perfulandia.pedidos.dto.ItemPedidoDTO;
import com.example.perfulandia.pedidos.repository.PedidosRepository;
import com.example.perfulandia.usuario.service.UsuarioService;
import com.example.perfulandia.model.InventarioModel;
// --- NUEVAS IMPORTACIONES PARA FEIGN CLIENTS ---
import com.example.perfulandia.pedidos.client.ProductoFeignClient;
import com.example.perfulandia.pedidos.client.InventarioFeignClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

@Service
public class PedidosServiceImpl implements PedidosService {

    private final PedidosRepository pedidosRepository;
    private final UsuarioService usuarioService;


    private final ProductoFeignClient productoClient;     // NUEVO
    private final InventarioFeignClient inventarioClient; // NUEVO

    @Autowired
    public PedidosServiceImpl(PedidosRepository pedidosRepository,
                              UsuarioService usuarioService,
                              // ProductoRepository productoRepository, // QUITAR
                              // InventarioService inventarioService,   // QUITAR
                              ProductoFeignClient productoClient,       // AÑADIR
                              InventarioFeignClient inventarioClient) { // AÑADIR
        this.pedidosRepository = pedidosRepository;
        this.usuarioService = usuarioService;

        this.productoClient = productoClient;         // AÑADIR
        this.inventarioClient = inventarioClient;       // AÑADIR
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
        // fechaPedido y estado se inicializan en el constructor de PedidosModel por defecto

        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDTO : requestDTO.items()) {
            if (itemDTO.cantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad para el producto ID " + itemDTO.productoId() + " debe ser positiva.");
            }

            // 1. Obtener producto usando Feign Client
            ProductoModel producto = productoClient.obtenerProductoPorId(itemDTO.productoId());
            if (producto == null) { // Feign podría devolver null o lanzar una excepción configurada si el servicio no responde o da 404
                throw new RuntimeException("Producto no encontrado con ID: " + itemDTO.productoId() + " a través del servicio de productos.");
            }

            // 2. Obtener inventario/stock usando Feign Client
            InventarioModel inventario = inventarioClient.obtenerInventarioPorProductoId(producto.getId());
            if (inventario == null || inventario.getCantidadDisponible() == null) {
                throw new RuntimeException("Inventario no encontrado o cantidad no disponible para el producto ID: " + producto.getId());
            }
            int stockDisponible = inventario.getCantidadDisponible();

            if (stockDisponible < itemDTO.cantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() +
                        ". Stock actual: " + stockDisponible + ", Solicitado: " + itemDTO.cantidad());
            }

            DetallePedidoModel detalle = new DetallePedidoModel();
            detalle.setProducto(producto); // Guardamos la referencia al producto (obtenido vía Feign)
            detalle.setCantidad(itemDTO.cantidad());
            detalle.setPrecioUnitarioAlMomentoDeCompra(BigDecimal.valueOf(producto.getPrecio()));
            BigDecimal subtotalItem = detalle.getPrecioUnitarioAlMomentoDeCompra().multiply(BigDecimal.valueOf(itemDTO.cantidad()));
            detalle.setSubtotal(subtotalItem);

            nuevoPedido.agregarDetalle(detalle);
            totalGeneral = totalGeneral.add(subtotalItem);

            // 3. Disminuir el stock del producto usando Feign Client
            // Esta llamada debe ser robusta. El servicio de inventario debe manejar su propia transacción.
            inventarioClient.disminuirStock(producto.getId(), itemDTO.cantidad());
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