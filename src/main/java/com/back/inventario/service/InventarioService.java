package com.back.inventario.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.inventario.dto.ProductoDTO;
import com.back.inventario.dto.ProductoEntradaDTO;
import com.back.inventario.dto.ProductoSalidaDTO;
import com.back.inventario.dto.RegistroEntradaResponseDTO;
import com.back.inventario.dto.RegistroSalidaResponseDTO;
import com.back.inventario.entities.Categoria;
import com.back.inventario.entities.Producto;
import com.back.inventario.entities.RegistroEntrada;
import com.back.inventario.entities.RegistroSalida;
import com.back.inventario.mapper.ProductoMapper;
import com.back.inventario.mapper.RegistroEntradaMapper;
import com.back.inventario.mapper.RegistroSalidaMapper;
import com.back.inventario.repositories.CategoriaRepository;
import com.back.inventario.repositories.ProductoRepository;
import com.back.inventario.repositories.RegistroEntradaRepository;
import com.back.inventario.repositories.RegistroSalidaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class InventarioService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private RegistroEntradaRepository registroEntradaRepository;
    
    @Autowired
    private RegistroSalidaRepository registroSalidaRepository;

    @Autowired
    private ProductoMapper mapper;

    @Autowired
    private RegistroEntradaMapper registroEntradaMapper;
    
    @Autowired
    private RegistroSalidaMapper registroSalidaMapper;

    /* PAGE PRODUCTOS Y CATEGORIAS */
    @Transactional(readOnly = true)
    public Page<Producto> listaDeProductos(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Categoria> listaDeCategorias(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return categoriaRepository.findAll(pageable);
    }
    /* LIST */
    @Transactional(readOnly = true)
    public List<Categoria> listaCategorias() {
        return categoriaRepository.findAll();
    }

    /* CREAR PRODUCTOS Y CATEGORIAS */
    @Transactional
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {

        // Validar que la categoría y su ID no sean null
        if (productoDTO.getCategoria() == null || productoDTO.getCategoria().getId() == null) {
            throw new IllegalArgumentException("El producto debe tener una categoría válida con un ID no nulo.");
        }

        // Buscar la categoría por su ID
        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoria().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Categoría con ID " + productoDTO.getCategoria().getId() + " no encontrada"));

        // Asignar la categoría al producto
        productoDTO.setCategoria(categoria);
        // Asignar entradas al stock y mapear DTO a producto
        Producto producto = new Producto();
        producto = mapper.productoDTOToProducto(productoDTO);
        producto.setStockCocina(productoDTO.getEntradas());
        producto.setStockVentas(productoDTO.getEntradas());
        producto.setStockGeneral(productoDTO.getEntradas());
        producto.setSalidasCocina(0);
        producto.setSalidasVentas(0);
        

        // Guardar el producto en la base de datos
        productoRepository.save(producto);

        return productoDTO;
    }
    
    /* CONTROL DE STOCK */
    /* *Entradas */
    @Transactional
    public Producto agregarEntradas(ProductoEntradaDTO productoEntradaDTO) {

        Optional<Producto> productoOptional = productoRepository.findById(productoEntradaDTO.getProductoId());

        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            producto.setEntradas(producto.getEntradas() + productoEntradaDTO.getEntradas());
            producto.setStockCocina(producto.getStockCocina() + productoEntradaDTO.getEntradas());
            producto.setStockVentas(producto.getStockVentas() + productoEntradaDTO.getEntradas());
            producto.setStockGeneral(producto.getStockGeneral() + productoEntradaDTO.getEntradas());
            // Registrar la entrada en la tabla RegistroEntrada
            RegistroEntrada registroEntrada = new RegistroEntrada();
            registroEntrada.setProducto(producto);
            registroEntrada.setCantidadEntrada(productoEntradaDTO.getEntradas());
            registroEntrada.setFechaEntrada(LocalDateTime.now());
            registroEntradaRepository.save(registroEntrada);
            return productoRepository.save(producto);
        } else {
            throw new RuntimeException("Producto con ID '" + productoEntradaDTO.getProductoId() + "' no encontrado.");
        }

    }
    // Buscar productos por fecha de entrada 
    @Transactional(readOnly = true)
    public List<RegistroEntradaResponseDTO> buscarEntradasPorFechaEntre(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<RegistroEntrada> registros = registroEntradaRepository.findByFechaEntradaBetween(fechaInicio, fechaFin);
        return registros.stream()
            .map(registroEntradaMapper::registroEntradaToResponseDTO) // Utiliza el mapper para convertir a DTO
            .collect(Collectors.toList());

    
    }


    /* *Salidas */
    @Transactional
    public Producto restarSalidas(ProductoSalidaDTO productoSalidaDTO) {

        Optional<Producto> productoOptional = productoRepository.findById(productoSalidaDTO.getProductoId());

        if (productoOptional.isPresent()) {

            Producto producto = productoOptional.get();

            if (producto.getStockCocina() >= productoSalidaDTO.getSalidas()) {
                int salidasActuales = (producto.getSalidasCocina() != null) ? producto.getSalidasCocina() : 0;
                producto.setSalidasCocina(salidasActuales + productoSalidaDTO.getSalidas());
                producto.setStockCocina(producto.getStockCocina() - productoSalidaDTO.getSalidas());

                RegistroSalida registroSalida = new RegistroSalida();
                registroSalida.setProducto(producto);
                registroSalida.setCantidadSalida(productoSalidaDTO.getSalidas());
                registroSalida.setFechaSalida(LocalDateTime.now());
                registroSalidaRepository.save(registroSalida);
                return productoRepository.save(producto);

            } else {
                throw new RuntimeException(
                        "Stock insuficiente para el producto con ID: " + productoSalidaDTO.getProductoId());
            }

        } else {
            throw new RuntimeException("Producto con ID '" + productoSalidaDTO.getProductoId() + "' no encontrado.");
        }

    }
    
    @Transactional(readOnly = true)
    public List<RegistroSalidaResponseDTO> buscarSalidasPorFechaEntre(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<RegistroSalida> registros = registroSalidaRepository.findByFechaSalidaBetween(fechaInicio, fechaFin);
        return registros.stream()
            .map(registroSalidaMapper::registroSalidaToResponseDTO) // Utiliza el mapper para convertir a DTO
            .collect(Collectors.toList());

    
    }
}
