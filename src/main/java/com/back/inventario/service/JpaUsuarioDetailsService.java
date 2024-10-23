package com.back.inventario.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.inventario.entities.Usuario;
import com.back.inventario.repositories.UsuarioRepository;

@Service
public class JpaUsuarioDetailsService implements UserDetailsService{
    //

    @Autowired
    private UsuarioRepository repository;
    
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {

        Usuario usuarioEncontrado = repository.findByNombre(nombre);

        if (usuarioEncontrado == null) {
            throw new UsernameNotFoundException(String.format("Nombre %s no existe en el sistema!", nombre));
        } 
        List<GrantedAuthority> authorities = usuarioEncontrado.getRoles().stream() //convertir la lista de roles en roles 
        .map(role -> new SimpleGrantedAuthority(role.getNombre()))                 //GrantedAuthority
        .collect(Collectors.toList());

        
        return new User(usuarioEncontrado.getNombre(),//puedes usar solo User de spring security.
        usuarioEncontrado.getPassword(), 
        usuarioEncontrado.isHabilitar(),
        true,
        true,
        true,
                authorities);
    }

    

}
