package com.example.perfulandia.pedidos.service;

import com.example.perfulandia.model.*;
import com.example.perfulandia.model.Role;
import com.example.perfulandia.pedidos.client.InventarioFeignClient;
import com.example.perfulandia.pedidos.client.ProductoFeignClient;
import com.example.perfulandia.pedidos.dto.CrearPedidoRequestDTO;
import com.example.perfulandia.pedidos.dto.ItemPedidoDTO;
import com.example.perfulandia.pedidos.repository.PedidosRepository;
import com.example.perfulandia.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidosServiceImplTest {

    @Mock
    private PedidosRepository pedidosRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ProductoFeignClient productoClient;
    @Mock
    private InventarioFeignClient inventarioClient;

    @InjectMocks
    private PedidosServiceImpl pedidosService;

    private UsuarioModel usuario;
    private ProductoModel producto;
    private InventarioModel inventario;
    private CrearPedidoRequestDTO pedidoRequest;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNombre("Usuario de Prueba");
        usuario.setEmail("user@example.com");
        usuario.setPassword("password");
        usuario.setRole(Role.ROLE_USER);

        producto = new ProductoModel();
        producto.setId(10L);
        producto.setNombre("Perfume Feign");
        producto.setDescripcion("Desde un servicio remoto");
        producto.setMarca("Feign Brand");
        producto.setCategoria("Test");
        producto.setPrecio(150.0);
        producto.setTamanioMl(100);

        inventario = new InventarioModel();
        inventario.setId(10L);
        inventario.setProducto(producto);
        inventario.setCantidadDisponible(20);

        ItemPedidoDTO item = new ItemPedidoDTO(10L, 5);
        pedidoRequest = new CrearPedidoRequestDTO(Collections.singletonList(item));
    }

    @Test
    void crearPedido_cuandoHayStock_debeCrearPedidoYDisminuirStock() {
        // Arrange
        when(usuarioService.buscarPorEmail("user@example.com")).thenReturn(Optional.of(usuario));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);
        when(inventarioClient.obtenerInventarioPorProductoId(10L)).thenReturn(inventario);
        when(inventarioClient.disminuirStock(10L, 5)).thenReturn(inventario);
        when(pedidosRepository.save(any(PedidosModel.class))).thenAnswer(invocation -> {
            PedidosModel pedidoGuardado = invocation.getArgument(0);
            pedidoGuardado.setId(100L);
            return pedidoGuardado;
        });

        // Act
        PedidosModel resultado = pedidosService.crearPedido(pedidoRequest, "user@example.com");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getDetalles().size());
        assertEquals(750.0, resultado.getTotalPedido().doubleValue());
        verify(inventarioClient, times(1)).disminuirStock(10L, 5);
        verify(pedidosRepository, times(1)).save(any(PedidosModel.class));
    }

    @Test
    void crearPedido_cuandoNoHayStockSuficiente_debeLanzarExcepcion() {
        // Arrange
        inventario.setCantidadDisponible(3); // Solo hay 3, pero se piden 5
        when(usuarioService.buscarPorEmail("user@example.com")).thenReturn(Optional.of(usuario));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);
        when(inventarioClient.obtenerInventarioPorProductoId(10L)).thenReturn(inventario);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidosService.crearPedido(pedidoRequest, "user@example.com");
        });
        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(pedidosRepository, never()).save(any(PedidosModel.class));
    }

    @Test
    void crearPedido_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        // Arrange
        when(usuarioService.buscarPorEmail("nouser@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidosService.crearPedido(pedidoRequest, "nouser@example.com");
        });
        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(productoClient, never()).obtenerProductoPorId(anyLong());
        verify(pedidosRepository, never()).save(any(PedidosModel.class));
    }

    @Test
    void crearPedido_cuandoProductoNoEsEncontradoPorFeign_debeLanzarExcepcion() {
        // Arrange
        when(usuarioService.buscarPorEmail("user@example.com")).thenReturn(Optional.of(usuario));
        when(productoClient.obtenerProductoPorId(99L)).thenReturn(null); // Feign devuelve null

        ItemPedidoDTO itemInvalido = new ItemPedidoDTO(99L, 1);
        CrearPedidoRequestDTO requestInvalida = new CrearPedidoRequestDTO(Collections.singletonList(itemInvalido));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidosService.crearPedido(requestInvalida, "user@example.com");
        });
        assertTrue(exception.getMessage().contains("Producto no encontrado con ID: 99"));
        verify(pedidosRepository, never()).save(any(PedidosModel.class));
    }
}