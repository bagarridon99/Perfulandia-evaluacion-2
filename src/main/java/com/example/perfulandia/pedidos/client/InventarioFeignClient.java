package com.example.perfulandia.pedidos.client; // O tu paquete de clientes

import com.example.perfulandia.model.InventarioModel; // Asegúrate que la ruta sea correcta
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity; // Para obtener la respuesta completa si es necesario
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map; // Para el DTO simple de stock

// url: La URL base del servicio de inventarios.
@FeignClient(name = "inventario-service-client", url = "http://localhost:8080/api/v1/inventarios")
public interface InventarioFeignClient {

    // Coincide con: @GetMapping("/producto/{productoId}/stock") en InventarioController
    // Nota: El endpoint en InventarioController devolvía un String JSON, así que podríamos necesitar
    // un DTO o Map para deserializarlo, o cambiar el endpoint para que devuelva un objeto más estructurado.
    // Por ahora, asumamos que devuelve un objeto que se puede mapear a Map<String, Integer> o un DTO.
    // sería mejor que InventarioController devolviera un DTO más claro.
    // Vamos a asumir que el controller devuelve un objeto simple para el stock.
    // Si InventarioController devuelve: ResponseEntity.ok().body("{\"productoId\": " + productoId + ", \"stockDisponible\": " + stock + "}");
    // Feign podría tener problemas para mapear eso directamente a un 'int'.
    // Sería mejor que el endpoint devuelva un JSON como {"stockDisponible": X}
    // O cambiar InventarioController para que devuelva el InventarioModel completo y sacar el stock de ahí.

    @GetMapping("/producto/{productoId}")
    InventarioModel obtenerInventarioPorProductoId(@PathVariable("productoId") Long productoId);
    // Ejemplo si el endpoint /stock devolviera: {"stockDisponible": 10}
    // @GetMapping("/producto/{productoId}/stock")
    // Map<String, Integer> obtenerStockDisponible(@PathVariable("productoId") Long productoId);


    // Coincide con: @PutMapping("/producto/{productoId}/incrementar") en InventarioController
    @PutMapping("/producto/{productoId}/incrementar")
    InventarioModel incrementarStock(@PathVariable("productoId") Long productoId, @RequestParam("cantidad") int cantidad);

    // Coincide con: @PutMapping("/producto/{productoId}/disminuir") en InventarioController
    @PutMapping("/producto/{productoId}/disminuir")
    InventarioModel disminuirStock(@PathVariable("productoId") Long productoId, @RequestParam("cantidad") int cantidad);
}