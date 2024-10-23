package com.back.inventario.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.back.inventario.entities.Usuario;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.back.inventario.security.TokenJwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    //intentar realizar la autenticacion. 
    @Override                                   //obtenemos el user y password                 //
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        /* Obtenemos el JSON y lo convertimos a un objeto de java */
        Usuario user = null;
        String username = null;
        String password = null;
        
        try {
            user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
            username = user.getNombre();
            password = user.getPassword();
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //validamos el username y password, cuando creamos el token
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
        password);
        
        System.out.println(authenticationManager.authenticate(authenticationToken));
        return authenticationManager.authenticate(authenticationToken);
    }

    //si todo sale correcto, le mandamos el token.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        User user = (User) authResult.getPrincipal();
        String username = user.getUsername();
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

        Claims claims = Jwts.claims()//
                .add("authorities", new ObjectMapper().writeValueAsString(roles))//pasando los roles como un json
                .add("username", username)
                .build();

        //generando el token(creando token)
        String token = Jwts.builder()
                .subject(username)
                .claims(claims)//agregacion extra
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora de ejecucion
                .issuedAt(new Date())
                .signWith(SECRET_KEY)
                .compact();
        //devolviendo el token al usuario al cliente(vista)
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        //lo pasamos a json, como una respuesta json.
        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("username", username);
        body.put("message", String.format("Hola %s has iniciado sesion con exito!", username));

        //mandando el json en la respuesta
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(200);
    }
    
    //todo lo contrario, si sale error en la autenticacion.
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Error en la autenticacion username o password incorrectos!");
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType(CONTENT_TYPE);
    }
}
