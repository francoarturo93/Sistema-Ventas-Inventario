package com.back.inventario.dto;

import lombok.Data;

@Data
public class RegistroEntradaResponseDTO {
    private Long id;
    private Long productoId; // Solo el ID del producto
    private String nombre; // Solo el ID del producto
    private int cantidadEntrada;
    private String fechaEntrada; // Mantener el formato original
}
