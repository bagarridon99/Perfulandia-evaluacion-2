package com.example.perfulandia.inventario.service;

import com.example.perfulandia.model.InventarioModel;
import com.example.perfulandia.model.ProductoModel;
import com.example.perfulandia.inventario.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private ProductoModel producto;
    private InventarioModel inventario;

    @BeforeEach
    void setUp() {
        // --- Objeto Producto de prueba ---
        producto = new ProductoModel();
        producto.setId(1L);
        producto.setNombre("Perfume para Inventario");

        // --- Objeto Inventario de prueba ---
        inventario = new InventarioModel();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidadDisponible(10);
    }

    @Test
    void incrementarStock_cuandoInventarioExiste_debeIncrementarCantidad() {
        // Arrange
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(InventarioModel.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        InventarioModel resultado = inventarioService.incrementarStock(1L, 5);

        // Assert
        assertNotNull(resultado);
        assertEquals(15, resultado.getCantidadDisponible());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    void incrementarStock_cuandoCantidadEsNegativa_debeLanzarExcepcion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            inventarioService.incrementarStock(1L, -5);
        });
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void disminuirStock_cuandoStockEsSuficiente_debeDisminuirCantidad() {
        // Arrange
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(InventarioModel.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        InventarioModel resultado = inventarioService.disminuirStock(1L, 3);

        // Assert
        assertNotNull(resultado);
        assertEquals(7, resultado.getCantidadDisponible());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    void disminuirStock_cuandoStockEsInsuficiente_debeLanzarExcepcion() {
        // Arrange
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventarioService.disminuirStock(1L, 20); // Intentamos sacar 20, pero solo hay 10
        });

        assertTrue(exception.getMessage().contains("No hay stock suficiente"));
        verify(inventarioRepository, never()).save(any(InventarioModel.class));
    }

    @Test
    void asegurarInventario_cuandoNoExiste_debeCrearNuevoInventario() {
        // Arrange
        ProductoModel productoNuevo = new ProductoModel();
        productoNuevo.setId(2L);
        when(inventarioRepository.findById(2L)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(InventarioModel.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        InventarioModel resultado = inventarioService.asegurarInventarioParaProducto(productoNuevo, 50);

        // Assert
        assertNotNull(resultado);
        assertEquals(50, resultado.getCantidadDisponible());
        assertEquals(2L, resultado.getProducto().getId());
        verify(inventarioRepository, times(1)).save(any(InventarioModel.class));
    }

    @Test
    void asegurarInventario_cuandoYaExiste_debeActualizarStock() {
        // Arrange
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(InventarioModel.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        InventarioModel resultado = inventarioService.asegurarInventarioParaProducto(producto, 100); // Actualizamos de 10 a 100

        // Assert
        assertNotNull(resultado);
        assertEquals(100, resultado.getCantidadDisponible());
        verify(inventarioRepository, times(1)).save(inventario);
    }
}