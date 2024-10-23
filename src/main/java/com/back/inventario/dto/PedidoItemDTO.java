package com.back.inventario.dto;

import lombok.Data;

@Data
public class PedidoItemDTO {

    private Long id;

    private PlatilloDTO platillo;

    private int cantidad;

}
