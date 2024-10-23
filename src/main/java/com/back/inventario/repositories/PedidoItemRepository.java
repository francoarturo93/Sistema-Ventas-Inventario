package com.back.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.inventario.entities.PedidoItem;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long>{

}
