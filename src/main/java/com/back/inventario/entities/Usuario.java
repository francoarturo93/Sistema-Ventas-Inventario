package com.back.inventario.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) //no se puede repetir, debe ser unico
    private String nombre;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//para no mostrar en la respuesta   
    private String password;

    @JsonIgnoreProperties({"usuarios", "handler", "hibernateLazyInitializer"})
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
      name = "usuarios_roles",
      joinColumns = @JoinColumn(name = "usuario_id"),
      inverseJoinColumns = @JoinColumn(name = "rol_id"),
      uniqueConstraints = { @UniqueConstraint(columnNames = {"usuario_id", "rol_id"})})
    private List<Rol> roles;

    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//para no mostrar en la respuesta   
    @Transient //no persistencia
    private boolean admin;

    //para dar acceso en un futuro(habilitado y deshabilitado)
    private boolean habilitar;

    public Usuario() {
      this.roles = new ArrayList<>();
    }

    @PrePersist
    public void prePersist() {
      habilitar = true;
    }
}
