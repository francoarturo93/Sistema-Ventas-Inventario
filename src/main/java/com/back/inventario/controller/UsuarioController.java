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

import com.back.inventario.entities.Usuario;
import com.back.inventario.exceptions.ValidationErrorHandler;
import com.back.inventario.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> list() {
        return usuarioService.findAll();
    }

    @PostMapping("/crear")
    public ResponseEntity<?> create(@Valid @RequestBody Usuario usuario, BindingResult result) {
        if (result.hasFieldErrors()) {
            // Si hay errores de validación, devolvemos un BAD REQUEST con los detalles de los errores
            return ValidationErrorHandler.handleValidationErrors(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(usuario));

    }
    
    @PostMapping("/registro")
    public ResponseEntity<?> register(@Valid @RequestBody Usuario usuario,  BindingResult result) {
        usuario.setAdmin(true);
        return create(usuario,result);

    }
}
