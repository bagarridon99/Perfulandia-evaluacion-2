package com.example.perfulandia.inventario.service;

import com.example.perfulandia.model.InventarioModel;
import com.example.perfulandia.model.ProductoModel;
import com.example.perfulandia.inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service // Le indica a Spring que esta clase es un bean de servicio
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    // private final ProductoRepository productoRepository; // Descomenta si lo necesitas para cargar ProductoModel

    @Autowired
    public InventarioServiceImpl(InventarioRepository inventarioRepository) { //, ProductoRepository productoRepository) {
        this.inventarioRepository = inventarioRepository;
        // this.productoRepository = productoRepository;
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
        // Intenta buscar si ya existe un inventario para este producto
        // El ID del inventario es el mismo que el del producto gracias a @MapsId
        Optional<InventarioModel> inventarioExistenteOpt = inventarioRepository.findById(producto.getId());

        InventarioModel inventario;
        if (inventarioExistenteOpt.isPresent()) {
            inventario = inventarioExistenteOpt.get();
            // Si ya existe, actualizamos su stock al stockInicial proporcionado.
            inventario.setCantidadDisponible(stockInicial);
        } else {
            // Si no existe, crea uno nuevo
            inventario = new InventarioModel();
            inventario.setId(producto.getId()); // Establece el ID del inventario igual al del producto
            inventario.setProducto(producto);   // Establece la relación bidireccional
            inventario.setCantidadDisponible(stockInicial);
        }
        return inventario; // Devuelve el inventario (nuevo o actualizado) listo para ser guardado
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