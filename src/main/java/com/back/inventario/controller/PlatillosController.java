package com.back.inventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.inventario.entities.Platillo;
import com.back.inventario.exceptions.ValidationErrorHandler;
import com.back.inventario.service.PlatilloService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/platillos")
public class PlatillosController {

    @Autowired
    private PlatilloService ventasService;

    /* *List */
    @GetMapping("/lista-platillos")
    public List<Platillo> listaPlatillos() {
        return ventasService.listaPlatillos();
    }

    /* *Crear */
    @PostMapping("/crear-platillos")
    public ResponseEntity<?> crearPlatillo(@Valid @RequestBody Platillo platillo, BindingResult result) {
        if (result.hasFieldErrors()) {
            // Si hay errores de validación, devolvemos un BAD REQUEST con los detalles de los errores
            return ValidationErrorHandler.handleValidationErrors(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ventasService.crearPlatillo(platillo));
            
    }
}
