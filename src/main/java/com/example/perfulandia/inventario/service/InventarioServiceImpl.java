package com.example.perfulandia.inventario.service;

import com.example.perfulandia.model.InventarioModel;
import com.example.perfulandia.model.ProductoModel;
import com.example.perfulandia.inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;

    @Autowired
    public InventarioServiceImpl(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventarioModel> obtenerInventarioPorProductoId(Long productoId) {
        return inventarioRepository.findById(productoId);
    }

    @Override
    @Transactional(readOnly = true)
    public int obtenerStockDisponible(Long productoId) {
        Optional<InventarioModel> inventarioOpt = obtenerInventarioPorProductoId(productoId);
        return inventarioOpt.map(InventarioModel::getCantidadDisponible).orElse(0);
    }

    @Override
    @Transactional
    public InventarioModel asegurarInventarioParaProducto(ProductoModel producto, int stockInicial) {
        if (producto == null || producto.getId() == null) {
            throw new IllegalArgumentException("El producto y su ID no pueden ser nulos para asegurar el inventario.");
        }
        if (stockInicial < 0) {
            throw new IllegalArgumentException("El stock inicial no puede ser negativo.");
        }

        InventarioModel inventario = obtenerOCrearActualizarInventario(producto, stockInicial);
        return inventarioRepository.save(inventario);
    }

    private InventarioModel obtenerOCrearActualizarInventario(ProductoModel producto, int stockInicial) {
        Optional<InventarioModel> inventarioExistenteOpt = inventarioRepository.findById(producto.getId());

        InventarioModel inventario;
        if (inventarioExistenteOpt.isPresent()) {
            inventario = inventarioExistenteOpt.get();
            inventario.setCantidadDisponible(stockInicial);
        } else {
            inventario = new InventarioModel();
            // NO establecemos el ID manualmente aquí: inventario.setId(producto.getId());
            // Al establecer la relación con @MapsId, Hibernate se encarga del ID.
            inventario.setProducto(producto);
            inventario.setCantidadDisponible(stockInicial);
        }
        return inventario;
    }

    @Override
    @Transactional
    public InventarioModel incrementarStock(Long productoId, int cantidadAIncrementar) {
        if (cantidadAIncrementar <= 0) {
            throw new IllegalArgumentException("La cantidad a incrementar debe ser positiva.");
        }
        InventarioModel inventario = inventarioRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("No se encontró inventario para el producto ID: " + productoId + ". Asegúrese de que el producto tenga un inventario asociado."));

        inventario.setCantidadDisponible(inventario.getCantidadDisponible() + cantidadAIncrementar);
        return inventarioRepository.save(inventario);
    }

    @Override
    @Transactional
    public InventarioModel disminuirStock(Long productoId, int cantidadADisminuir) {
        if (cantidadADisminuir <= 0) {
            throw new IllegalArgumentException("La cantidad a disminuir debe ser positiva.");
        }
        InventarioModel inventario = inventarioRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("No se encontró inventario para el producto ID: " + productoId + ". Asegúrese de que el producto tenga un inventario asociado."));

        int stockActual = inventario.getCantidadDisponible();
        if (stockActual < cantidadADisminuir) {
            throw new RuntimeException("No hay stock suficiente para el producto ID: " + productoId +
                    ". Stock actual: " + stockActual + ", Solicitado a disminuir: " + cantidadADisminuir);
        }

        inventario.setCantidadDisponible(stockActual - cantidadADisminuir);
        return inventarioRepository.save(inventario);
    }
}