package com.back.inventario.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.inventario.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    Optional<Cliente> findByNombre(String nombre);
}
