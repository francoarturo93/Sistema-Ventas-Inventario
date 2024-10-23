package com.back.inventario.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.inventario.entities.RegistroEntrada;

public interface RegistroEntradaRepository extends JpaRepository<RegistroEntrada, Long> {
    List<RegistroEntrada> findByFechaEntradaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}

