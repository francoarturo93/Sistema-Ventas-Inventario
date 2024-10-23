package com.back.inventario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.inventario.entities.Grupo;
import com.back.inventario.entities.Platillo;
import com.back.inventario.entities.Producto;
import com.back.inventario.repositories.GrupoRepository;
import com.back.inventario.repositories.PlatilloRepository;
import com.back.inventario.repositories.ProductoRepository;

@Service
public class PlatilloService {

    @Autowired
    private PlatilloRepository platilloRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /* *List */
    @Transactional(readOnly = true)
    public List<Platillo> listaPlatillos() {
        return platilloRepository.findAll();
    }

    /* *Crear Platillo */
    @Transactional
    public Platillo crearPlatillo(Platillo platillo) {
        Optional<Grupo> grupo = grupoRepository.findById(platillo.getGrupo().getId());
        if (grupo.isPresent()) {
            platillo.setGrupo(grupo.get());
        } else {
            throw new IllegalArgumentException("Grupo no encontrado");
        }

        // Verificar y asignar productos
        if (platillo.getProductos() != null && !platillo.getProductos().isEmpty()) {
            List<Producto> productosValidados = new ArrayList<>();
            for (Producto producto : platillo.getProductos()) {
                Optional<Producto> productoExistente = productoRepository.findById(producto.getId());
                if (productoExistente.isPresent()) {
                    productosValidados.add(productoExistente.get());
                } else {
                    throw new IllegalArgumentException("Producto no encontrado: " + producto.getNombre());
                }
            }
            platillo.setProductos(productosValidados);
        }
        return platilloRepository.save(platillo);
    }
}
