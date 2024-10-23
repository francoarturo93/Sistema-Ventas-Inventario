package com.back.inventario.dto;

import lombok.Data;

@Data
public class ProductoEntradaDTO {
    // Id del producto a buscar
    private Long productoId;

    // Cantidad de productos que entran al stock
    private Integer entradas;

}
