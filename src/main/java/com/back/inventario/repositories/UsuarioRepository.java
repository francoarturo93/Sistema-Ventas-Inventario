package com.back.inventario.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.inventario.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    boolean existsByNombre(String nombre);
    Usuario findByNombre(String nombre);
}
