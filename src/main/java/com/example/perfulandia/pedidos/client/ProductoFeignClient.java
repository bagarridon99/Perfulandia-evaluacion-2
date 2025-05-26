package com.example.perfulandia.pedidos.client; // O tu paquete de clientes

import com.example.perfulandia.model.ProductoModel; // Asegúrate que la ruta sea correcta
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "producto-service-client", url = "http://localhost:8080/api/v1/productos")
public interface ProductoFeignClient {

    @GetMapping("/{id}")
    ProductoModel obtenerProductoPorId(@PathVariable("id") Long id);

}