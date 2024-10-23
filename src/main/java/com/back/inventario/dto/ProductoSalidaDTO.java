package com.back.inventario.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ProductoSalidaDTO {
    private Long productoId;

    private Integer salidas;

    private Date fecha;
}
