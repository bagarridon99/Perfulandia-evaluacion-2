package com.example.perfulandia.pedidos.controller;

import com.example.perfulandia.model.PedidosModel;
import com.example.perfulandia.model.enums.EstadoPedido;
import com.example.perfulandia.pedidos.dto.CrearPedidoRequestDTO;
import com.example.perfulandia.pedidos.service.PedidosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidosController {

    private final PedidosService pedidosService;

    @Autowired
    public PedidosController(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @PostMapping
    public ResponseEntity<?> crearNuevoPedido(@RequestBody CrearPedidoRequestDTO requestDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o detalles de usuario no disponibles.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailUsuario = userDetails.getUsername(); // Asumimos que el username es el email

        try {
            PedidosModel pedidoCreado = pedidosService.crearPedido(requestDTO, emailUsuario);
            return new ResponseEntity<>(pedidoCreado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // Para stock, producto no encontrado, etc.
        }
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> obtenerMisPedidos(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o detalles de usuario no disponibles.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailUsuario = userDetails.getUsername();

        List<PedidosModel> pedidos = pedidosService.obtenerPedidosPorUsuario(emailUsuario);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long pedidoId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o detalles de usuario no disponibles.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String emailUsuario = userDetails.getUsername();

        Optional<PedidosModel> pedidoOpt = pedidosService.obtenerPedidoPorIdYUsuario(pedidoId, emailUsuario);
        if (pedidoOpt.isPresent()) {
            return ResponseEntity.ok(pedidoOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado o no pertenece al usuario.");
        }
    }

    // (ADMIN) Endpoint para actualizar estado - Asegúrate de protegerlo en SecurityConfig
    @PutMapping("/{pedidoId}/estado")
    public ResponseEntity<?> actualizarEstadoPedido(
            @PathVariable Long pedidoId,
            @RequestParam EstadoPedido nuevoEstado) {
        // Aquí deberías verificar si el usuario es ADMIN antes de llamar al servicio.
        // Por ahora, el servicio lo maneja, pero el controlador también debería protegerlo.
        // Ejemplo: if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) { return ... }
        try {
            PedidosModel pedidoActualizado = pedidosService.actualizarEstadoPedidoAdmin(pedidoId, nuevoEstado);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}