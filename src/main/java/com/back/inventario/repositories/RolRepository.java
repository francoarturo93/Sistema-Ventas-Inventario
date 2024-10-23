package com.back.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.inventario.entities.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Rol findByNombre(String nombre);
}
