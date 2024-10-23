package com.back.inventario.controller;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.inventario.dto.ProductoDTO;
import com.back.inventario.dto.ProductoEntradaDTO;
import com.back.inventario.dto.ProductoSalidaDTO;
import com.back.inventario.dto.RegistroEntradaResponseDTO;
import com.back.inventario.dto.RegistroSalidaResponseDTO;
import com.back.inventario.entities.Categoria;
import com.back.inventario.entities.Producto;

import com.back.inventario.exceptions.ValidationErrorHandler;
import com.back.inventario.service.InventarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    /* PAGE */
    @GetMapping("/pageproductos")
    public Page<Producto> obtenerProductos(@RequestParam int page, @RequestParam int size) {
        return inventarioService.listaDeProductos(page, size);
    }
    @GetMapping("/pagecategorias")
    public Page<Categoria> obtenerCategorias(@RequestParam int page, @RequestParam int size) {
        return inventarioService.listaDeCategorias(page, size);
    }

    /* LIST */
    @GetMapping("/listcategorias")
    public List<Categoria> listaCategorias() {
        return inventarioService.listaCategorias();
    }
    @GetMapping("/buscarEntradasPorFecha")
    public List<RegistroEntradaResponseDTO> buscarEntradasPorFecha(@RequestParam String fecha) {
        // Definir el formato personalizado de la fecha "dd-MM-yy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

        // Parsear el string a LocalDate con el formato definido
        LocalDate localDate = LocalDate.parse(fecha, formatter);

        // Obtener la fecha de inicio y fin para la búsqueda
        LocalDateTime fechaInicio = localDate.atStartOfDay(); // 00:00:00 del día especificado
        LocalDateTime fechaFin = localDate.plusDays(1).atStartOfDay(); // 00:00:00 del siguiente día

        return inventarioService.buscarEntradasPorFechaEntre(fechaInicio, fechaFin);
    }

    @GetMapping("/buscarSalidasPorFecha")
    public List<RegistroSalidaResponseDTO> buscarSalidasPorFecha(@RequestParam String fecha) {
        // Definir el formato personalizado de la fecha "dd-MM-yy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

        // Parsear el string a LocalDate con el formato definido
        LocalDate localDate = LocalDate.parse(fecha, formatter);

        // Obtener la fecha de inicio y fin para la búsqueda
        LocalDateTime fechaInicio = localDate.atStartOfDay(); // 00:00:00 del día especificado
        LocalDateTime fechaFin = localDate.plusDays(1).atStartOfDay(); // 00:00:00 del siguiente día

        return inventarioService.buscarSalidasPorFechaEntre(fechaInicio, fechaFin);
    }

    /* CREAR */
    @PostMapping("/crearproductos")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoDTO productoDTO, BindingResult result) {
        if (result.hasFieldErrors()) {
            // Si hay errores de validación, devolvemos un BAD REQUEST con los detalles de los errores
            return ValidationErrorHandler.handleValidationErrors(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crearProducto(productoDTO));
    }

    /* CONTROL DE STOCK */
    @PostMapping("/entradas")
    public ResponseEntity<?> agregarEntradas(@Valid @RequestBody ProductoEntradaDTO productoEntradaDTO,
            BindingResult result) {
        if (result.hasFieldErrors()) {
            // Si hay errores de validación, devolvemos un BAD REQUEST con los detalles de los errores
            return ValidationErrorHandler.handleValidationErrors(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.agregarEntradas(productoEntradaDTO));
    }
    
    @PostMapping("/salidas")
    public ResponseEntity<?> registrarSalidas(@Valid @RequestBody ProductoSalidaDTO productoSalidaDTO, BindingResult result) {
        if (result.hasFieldErrors()) {
            // Si hay errores de validación, devolvemos un BAD REQUEST con los detalles de los errores
            return ValidationErrorHandler.handleValidationErrors(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.restarSalidas(productoSalidaDTO));
    }
    
}
