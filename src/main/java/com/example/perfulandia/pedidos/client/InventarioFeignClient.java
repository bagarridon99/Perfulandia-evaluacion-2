package com.example.perfulandia.pedidos.client; // O tu paquete de clientes

import com.example.perfulandia.model.InventarioModel; // Asegúrate que la ruta sea correcta
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity; // Para obtener la respuesta completa si es necesario
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map; // Para el DTO simple de stock

@FeignClient(name = "inventario-service-client", url = "http://localhost:8080/api/v1/inventarios")
public interface InventarioFeignClient {



    @GetMapping("/producto/{productoId}")
    InventarioModel obtenerInventarioPorProductoId(@PathVariable("productoId") Long productoId);



    @PutMapping("/producto/{productoId}/incrementar")
    InventarioModel incrementarStock(@PathVariable("productoId") Long productoId, @RequestParam("cantidad") int cantidad);

    @PutMapping("/producto/{productoId}/disminuir")
    InventarioModel disminuirStock(@PathVariable("productoId") Long productoId, @RequestParam("cantidad") int cantidad);
}