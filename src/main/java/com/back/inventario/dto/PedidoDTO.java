package com.back.inventario.dto;

import java.util.List;

import com.back.inventario.entities.Cliente;

import com.back.inventario.entities.TipoPedido;

public class PedidoDTO {
    private Long id;

    private Cliente cliente;
    
    private String estado; // Abierto, Cerrado

    private TipoPedido tipoPedido; // Enum PARA_LLEVAR, EN_SALON


    private List<PedidoItemDTO> items;
    
    private Double total;
}
