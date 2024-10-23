package com.back.inventario.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.back.inventario.entities.Cliente;
import com.back.inventario.entities.Pedido;
import com.back.inventario.entities.PedidoItem;
import com.back.inventario.entities.Platillo;
import com.back.inventario.entities.Producto;
import com.back.inventario.repositories.ClienteRepository;
import com.back.inventario.repositories.PedidoRepository;
import com.back.inventario.repositories.PlatilloRepository;
import com.back.inventario.repositories.ProductoRepository;

@Service
public class VentasService {

    @Autowired
    private ClienteRepository clienteRepository;


    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PlatilloRepository platilloRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /* *List Pedidos */
    @Transactional(readOnly = true)
    public List<Pedido> listaPedidos() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PedidoItem> listaSoloPedidos() {
        List<Pedido> pedidos = pedidoRepository.findAll();

        Collections.reverse(pedidos);

        List<PedidoItem> items = pedidos.stream()
                .flatMap(pedido -> pedido.getItems().stream()) // Flatten la lista de items de cada pedido
                .collect(Collectors.toList());
        return items;
    }

    /* *Crear Pedido */
    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        Cliente cliente = new Cliente();
        Cliente nuevoCliente = new Cliente();
        // System.out.println(pedido.getCliente().getId()+"holi");
        Optional<Cliente> clieOptional = clienteRepository.findByNombre(pedido.getCliente().getNombre());
        if(!clieOptional.isPresent()){
            nuevoCliente.setNombre(pedido.getCliente().getNombre());
            // System.out.println(pedido.getCliente().getNumero());
            nuevoCliente.setNumero(pedido.getCliente().getNumero());
            nuevoCliente = clienteRepository.save(nuevoCliente);
            pedido.setCliente(nuevoCliente);
            
        } else {
            cliente = clieOptional.get();
            // System.out.println(cliente.getNombre());
            // System.out.println(cliente.getNumero() + "-" + cliente.getNombre() + "-" + cliente.getId()+"-"+pedido.getCliente().getNumero());
            // cliente.setNumero();
            if (pedido.getCliente().getNumero() != 0) {
                cliente.setNumero(pedido.getCliente().getNumero());
            }
            pedido.setCliente(cliente);
        }
        pedido.setEstado("ABIERTO");
        pedido.setItems(new ArrayList<>());
        pedido.setTotal(0.0);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido agregarPlatilloAPedido(Long pedidoId, PedidoItem nuevoItem) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new IllegalArgumentException("Pedido no encontrado"));
        Platillo platillo = platilloRepository.findById(nuevoItem.getPlatillo().getId()).get();
        
        nuevoItem.getPlatillo().setNombre(platillo.getNombre());
        nuevoItem.getPlatillo().setPrecio(platillo.getPrecio());

        // Recorrer los productos asociados al platillo
        for (Producto producto : platillo.getProductos()) {
            // Cantidad de producto a descontar del stock
            int cantidadUsadaPorPlatillo = nuevoItem.getCantidad();

            // Verificar si hay suficiente stock
            if (producto.getStockCocina() < cantidadUsadaPorPlatillo) {
                throw new IllegalArgumentException("No hay suficiente stock para el producto: " + producto.getNombre());
            }

            // Restar del stock
            producto.setSalidasVentas(producto.getSalidasVentas() + cantidadUsadaPorPlatillo);
            producto.setStockVentas(producto.getStockVentas() - cantidadUsadaPorPlatillo);

            // Guardar el producto con el stock actualizado
            productoRepository.save(producto);
        }

        pedido.setTotal(nuevoItem.getCantidad() * nuevoItem.getPlatillo().getPrecio() + pedido.getTotal());
        pedido.getItems().add(nuevoItem);
        return pedidoRepository.save(pedido);
    }
    
    @Transactional
    public Pedido finalizarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new IllegalArgumentException("Pedido no encontrado"));
        pedido.setEstado("FINALIZADO");
        return pedidoRepository.save(pedido);
    }

}
