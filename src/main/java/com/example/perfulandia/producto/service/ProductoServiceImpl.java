package com.example.perfulandia.producto.service;

import com.example.perfulandia.model.ProductoModel;
import com.example.perfulandia.producto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public ProductoModel anadirProducto(ProductoModel producto) {
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoModel> buscarProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoModel> buscarTodosLosProductos() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional
    public ProductoModel editarProducto(Long id, ProductoModel productoConNuevosDatos) {
        ProductoModel productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id + " para editar."));

        productoExistente.setNombre(productoConNuevosDatos.getNombre());
        productoExistente.setDescripcion(productoConNuevosDatos.getDescripcion());
        productoExistente.setMarca(productoConNuevosDatos.getMarca());
        productoExistente.setCategoria(productoConNuevosDatos.getCategoria());
        productoExistente.setPrecio(productoConNuevosDatos.getPrecio());
        productoExistente.setTamanioMl(productoConNuevosDatos.getTamanioMl());
        return productoRepository.save(productoExistente);
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id + " para eliminar.");
        }
        productoRepository.deleteById(id);
    }
}