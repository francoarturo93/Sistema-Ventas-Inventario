package com.back.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.inventario.entities.Platillo;

public interface PlatilloRepository extends JpaRepository<Platillo, Long>{

}
