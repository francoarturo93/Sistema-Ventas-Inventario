package com.back.inventario.dto;

import lombok.Data;

@Data
public class RegistroSalidaResponseDTO {
    private Long id;
    private Long productoId; // Solo el ID del producto
    private String nombre; // Solo el ID del producto
    private int cantidadSalida;
    private String fechaSalida;
}
