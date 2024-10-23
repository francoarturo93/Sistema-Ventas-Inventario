package com.back.inventario.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "salidas  ")
@Data
public class RegistroSalida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"registrosEntrada", "handler", "hibernateLazyInitializer"})//  elimina el bucle de esta entidad
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private int cantidadSalida;

    private LocalDateTime fechaSalida;
}
