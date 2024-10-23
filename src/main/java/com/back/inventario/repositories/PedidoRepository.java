package com.back.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.inventario.entities.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long>{

}
