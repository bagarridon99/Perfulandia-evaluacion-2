package com.example.perfulandia.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
public record CrearPedidoRequestDTO(
        @NotEmpty
        @Valid
        List<ItemPedidoDTO>items
) {

    public CrearPedidoRequestDTO {
        if (items == null || items.isEmpty()) {
            throw new ValidationException("El pedido debe contener al menos un item");
        }
    }
}