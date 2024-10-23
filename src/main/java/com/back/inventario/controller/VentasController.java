package com.back.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.inventario.entities.Pedido;
import com.back.inventario.entities.PedidoItem;
import com.back.inventario.exceptions.ValidationErrorHandler;
import com.back.inventario.service.VentasService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/ventas")
public class VentasController {

    @Autowired
    private VentasService ventasService;

    /* *LIST     */
    @GetMapping("/lista-pedidos")
    public List<Pedido> listaPedidos() {
        return ventasService.listaPedidos();
    }

    @GetMapping("/lista-solo-pedidos")
    public List<PedidoItem> listarSoloPedidos() {
        return ventasService.listaSoloPedidos();
    }

    /* *CREATE   */
    @PostMapping("/crear-pedidos")
    public ResponseEntity<?> crearPedido(@Valid @RequestBody Pedido pedido, BindingResult result) {
        if (result.hasFieldErrors()) {
            // Si hay errores de validación, devolvemos un BAD REQUEST con los detalles de los errores
            return ValidationErrorHandler.handleValidationErrors(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.crearPedido(pedido));
    }

    /* Agregar platillo a un pedido específico */
    @PostMapping("/agregar-platillo-pedido/{pedidoId}")
    public ResponseEntity<?> agregarPlatilloAPedido(@Valid @RequestBody PedidoItem nuevoItem, BindingResult result,
            @PathVariable Long pedidoId) {
        if (result.hasFieldErrors()) {
            return ValidationErrorHandler.handleValidationErrors(result);
        }

        Pedido pedidoActualizado = ventasService.agregarPlatilloAPedido(pedidoId, nuevoItem);
        return ResponseEntity.ok(pedidoActualizado);
    }
    
    @PostMapping("/finalizar-pedido")
    public ResponseEntity<?> finalizarPedido(@RequestParam Long pedidoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.finalizarPedido(pedidoId));
    }
}
