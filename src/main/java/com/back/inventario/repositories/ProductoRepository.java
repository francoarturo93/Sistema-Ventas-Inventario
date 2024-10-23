package com.back.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;


import com.back.inventario.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{
}
