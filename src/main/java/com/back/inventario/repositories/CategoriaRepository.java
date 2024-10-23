package com.back.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.inventario.entities.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

}
