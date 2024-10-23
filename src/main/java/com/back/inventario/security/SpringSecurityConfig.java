package com.back.inventario.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.back.inventario.security.filter.JwtAuthenticationFilter;
import com.back.inventario.security.filter.JwtValidationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled=true)
public class SpringSecurityConfig {
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();//devolviendo una implementacion
    }

    //filtro donde se valida los request, autorizar permisos o denegar
    @Bean                           //para dar seguridad a las peticiones http
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests((authz) -> authz
                //crea usuarios administradores
                .requestMatchers(HttpMethod.POST, "/api/usuarios/registro").permitAll()//colocamos la ruta(http) que queremos dar seguridad o permitir. 
                //crea usuarios con los roles administracion, caja, camareros y cocina
                .requestMatchers(HttpMethod.POST, "/api/usuarios/crear").hasAuthority("ROL_ADMIN")//colocamos la ruta(http) que queremos dar seguridad o permitir. 
                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAuthority("ROL_ADMIN")
                /* *PERMISOS VENTAS*/
                // CREAR PEDIDOS
                .requestMatchers(HttpMethod.POST, "/api/ventas/crear-pedidos").hasAnyAuthority("ROL_CAJA","ROL_ADMIN", "ROL_MOZO")
                // AGREGAR PLATILLOS A PEDIDO
                .requestMatchers(HttpMethod.POST, "/api/ventas/agregar-platillo-pedido/{pedidoId}").hasAnyAuthority("ROL_CAJA","ROL_ADMIN", "ROL_MOZO")
                // FINALIZAR COMPRA
                .requestMatchers(HttpMethod.POST, "/api/ventas/finalizar-pedido").hasAnyAuthority("ROL_CAJA","ROL_ADMIN")
                // LISTA PEDIDOS
                .requestMatchers(HttpMethod.POST, "/api/ventas/lista-pedidos").hasAnyAuthority("ROL_CAJA","ROL_ADMIN","ROL_MOZO","ROL_COCINA")
                // LISTA SOLO PEDIDOS
                .requestMatchers(HttpMethod.POST, "/api/ventas/lista-solo-pedidos").hasAnyAuthority("ROL_CAJA","ROL_ADMIN","ROL_MOZO","ROL_COCINA")
                // CREAR PLATILLOS
                .requestMatchers(HttpMethod.POST, "/api/platillos/crear-platillos").hasAuthority("ROL_ADMIN")
                
                /* *PERMISOS INVENTARIO */
                .requestMatchers("/api/inventario/**").hasAuthority("ROL_ADMIN")
                // CREAR PRODUCTOS
                // REGISTRAR ENTRADAS
                .requestMatchers(HttpMethod.POST, "/api/inventario/entradas").hasAnyAuthority("ROL_ADMIN","ROL_COCINA")
                // REGISTRAR SALIDAS
                .requestMatchers(HttpMethod.POST, "/api/inventario/salidas").hasAnyAuthority("ROL_ADMIN","ROL_COCINA")
                // BUSCAR ENTRADAS POR FECHA
                .requestMatchers(HttpMethod.POST, "/api/inventario/buscarEntradasPorFecha").hasAnyAuthority("ROL_ADMIN","ROL_COCINA")
                // BUSCAR SALIDAS POR FECHA
                .requestMatchers(HttpMethod.POST, "/api/inventario/buscarSalidasPorFecha").hasAnyAuthority("ROL_ADMIN","ROL_COCINA")
                // PAGINACION PRODUCTOS
                .requestMatchers(HttpMethod.POST, "/api/inventario/pageproductos").hasAnyAuthority("ROL_ADMIN","ROL_COCINA")
                // PAGINACION CATEGORIAS
                .requestMatchers(HttpMethod.POST, "/api/inventario/pagecategorias").hasAnyAuthority("ROL_ADMIN", "ROL_COCINA")
                
                .anyRequest().authenticated())//Cualquier otro request necesita autenticacion
                
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))//autenticarse, verifica el permiso.
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())//costumizar, desabilita(jsp, thymilif, formullarios) para solo trabajar con apirest
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
    
    //despues de crear JwtAuthenticationFilter lo configuramos.

    // 3° filtro(ultimo):
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
}
