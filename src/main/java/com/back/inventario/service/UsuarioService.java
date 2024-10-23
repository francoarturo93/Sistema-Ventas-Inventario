package com.back.inventario.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.inventario.entities.Rol;
import com.back.inventario.entities.Usuario;
import com.back.inventario.repositories.RolRepository;
import com.back.inventario.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }


    @Transactional
    public Usuario save(Usuario usuario) {// le dara acceso

        List<Rol> listRoles = new ArrayList<>();
        System.out.println(usuario.isAdmin());
        if (usuario.isAdmin()) {
            Rol rolAdmin = rolRepository.findByNombre("ROL_ADMIN");
            if (rolAdmin != null) {
                listRoles.add(rolAdmin);
            } else {
                throw new IllegalArgumentException("Rol ROL_ADMIN no encontrado");
            }
        } else {
            for (Rol rolInput : usuario.getRoles()) {
                String nombreRol = "ROL_" + rolInput.getNombre().toUpperCase();
                Rol rol = rolRepository.findByNombre(nombreRol);
    
                if (rol == null) {
                    throw new IllegalArgumentException("El rol especificado no existe: " + nombreRol);
                }
                listRoles.add(rol);
            }
        }

        // Asignamos el rol al usuario
        usuario.setRoles(listRoles); // Convertir a lista

        // Encriptamos la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guardamos el usuario
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return usuarioRepository.existsByNombre(nombre);
    }
}
