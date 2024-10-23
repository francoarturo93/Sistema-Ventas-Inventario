package com.back.inventario.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.inventario.entities.RegistroSalida;

public interface RegistroSalidaRepository extends JpaRepository<RegistroSalida, Long>{
    List<RegistroSalida> findByFechaSalidaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
