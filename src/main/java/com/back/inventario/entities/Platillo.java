package com.back.inventario.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "platillos")
@Data
public class Platillo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer precio;

    @JsonIgnoreProperties({ "platillos", "handler", "hibernateLazyInitializer" }) // elimina el bucle de esta entidad
    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    // @JsonIgnoreProperties({"platillos", "handler", "hibernateLazyInitializer"})
    @JsonIgnore
    @ManyToMany()
    @JoinTable(
        name = "platillo_producto",
        joinColumns = @JoinColumn(name = "platillo_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos;
}
