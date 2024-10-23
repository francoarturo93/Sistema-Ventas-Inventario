# Sistema de Gestión de Inventario y Ventas

Este proyecto es un sistema de gestión de inventario y ventas para un restaurante, construido con Spring Boot 3.2, Maven y MySQL. Proporciona la funcionalidad para gestionar productos, categorías, pedidos, platillos y usuarios, con un control de stock eficiente para entradas y salidas de productos. Además, incorpora autenticación y autorización mediante Spring Security y JWT.

## Características

- **Gestión de inventario:** Crear productos y categorías, registrar entradas y salidas de productos, y controlar el stock.
- **Gestión de ventas:** Crear pedidos, agregar platillos a los pedidos, finalizar ventas, y listar pedidos para diferentes roles (mozo, caja, cocina).
- **Autenticación y autorización:** Integración de Spring Security con JWT, con roles como `ROL_ADMIN`, `ROL_CAJA`, `ROL_MOZO`, y `ROL_COCINA`.
- **Paginación:** Paginación de listados de productos y categorías.
- **CORS y CSRF:** Configuración de CORS para permitir solicitudes externas, y CSRF deshabilitado para trabajar exclusivamente con API REST.

---

## Tecnologías

- **Backend:** Spring Boot 3.2.x, Spring Security, JWT, JPA, MapStruct.
- **Base de Datos:** MySQL
- **Autenticación:** Spring Security con JWT
- **Dependencias principales:**
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-security`
  - `mapstruct` para el mapeo entre entidades y DTOs
  - `lombok` para simplificar el código
  - `jjwt` para la generación y validación de tokens JWT

---

## Configuración de Seguridad
El sistema usa **Spring Security** para proteger las rutas de la API según los roles definidos:

- **ROL_ADMIN**: Acceso completo para gestionar inventario, productos, categorías y usuarios.
- **ROL_CAJA**: Permite registrar ventas, gestionar pedidos y finalizar compras.
- **ROL_MOZO**: Puede crear y agregar platillos a los pedidos.
- **ROL_COCINA**: Tiene acceso para gestionar el inventario y consultar pedidos.

La seguridad está configurada en la clase `SpringSecurityConfig`, que incluye filtros de autenticación y validación de JWT.

---

## Permisos y Rutas Protegidas
### 1. Usuarios

- **Crear usuario (registro)**  
  `POST /api/usuarios/registro`  
  _Permiso_: `permitAll()` (Acceso público)

- **Crear usuario con roles específicos**  
  `POST /api/usuarios/crear`  
  _Permiso_: `hasAuthority("ROL_ADMIN")` (Solo administrador)

- **Lista de usuarios**  
  `GET /api/usuarios`  
  _Permiso_: `hasAuthority("ROL_ADMIN")` (Solo administrador)

---

### 2. Ventas

- **Crear pedidos**  
  `POST /api/ventas/crear-pedidos`  
  _Permiso_: `hasAnyAuthority("ROL_CAJA", "ROL_ADMIN", "ROL_MOZO")`

- **Agregar platillos a pedido**  
  `POST /api/ventas/agregar-platillo-pedido/{pedidoId}`  
  _Permiso_: `hasAnyAuthority("ROL_CAJA", "ROL_ADMIN", "ROL_MOZO")`

- **Finalizar compra**  
  `POST /api/ventas/finalizar-pedido`  
  _Permiso_: `hasAnyAuthority("ROL_CAJA", "ROL_ADMIN")`

- **Lista de pedidos**  
  `POST /api/ventas/lista-pedidos`  
  _Permiso_: `hasAnyAuthority("ROL_CAJA", "ROL_ADMIN", "ROL_MOZO", "ROL_COCINA")`

- **Lista de solo pedidos**  
  `POST /api/ventas/lista-solo-pedidos`  
  _Permiso_: `hasAnyAuthority("ROL_CAJA", "ROL_ADMIN", "ROL_MOZO", "ROL_COCINA")`

---

### 3. Platillos

- **Crear platillos**  
  `POST /api/platillos/crear-platillos`  
  _Permiso_: `hasAuthority("ROL_ADMIN")` (Solo administrador)

---

### 4. Inventario

- **Acceso general a inventario**  
  `GET, POST /api/inventario/**`  
  _Permiso_: `hasAuthority("ROL_ADMIN")` (Solo administrador)

- **Registrar entradas**  
  `POST /api/inventario/entradas`  
  _Permiso_: `hasAnyAuthority("ROL_ADMIN", "ROL_COCINA")`

- **Registrar salidas**  
  `POST /api/inventario/salidas`  
  _Permiso_: `hasAnyAuthority("ROL_ADMIN", "ROL_COCINA")`

- **Buscar entradas por fecha**  
  `POST /api/inventario/buscarEntradasPorFecha`  
  _Permiso_: `hasAnyAuthority("ROL_ADMIN", "ROL_COCINA")`

- **Buscar salidas por fecha**  
  `POST /api/inventario/buscarSalidasPorFecha`  
  _Permiso_: `hasAnyAuthority("ROL_ADMIN", "ROL_COCINA")`

- **Paginación de productos**  
  `POST /api/inventario/pageproductos`  
  _Permiso_: `hasAnyAuthority("ROL_ADMIN", "ROL_COCINA")`

- **Paginación de categorías**  
  `POST /api/inventario/pagecategorias`  
  _Permiso_: `hasAnyAuthority("ROL_ADMIN", "ROL_COCINA")`

---

### Ruta Protegida Genérica

- **Cualquier otro request**  
  _Permiso_: `anyRequest().authenticated()` (Autenticación requerida para cualquier otra ruta)

---

## Notas

- El administrador (`ROL_ADMIN`) tiene acceso a todas las rutas y puede realizar cualquier acción en el sistema.
- El personal de caja (`ROL_CAJA`) tiene acceso a la creación y finalización de pedidos.
- Los mozos (`ROL_MOZO`) pueden crear pedidos y agregar platillos a pedidos existentes.
- El personal de cocina (`ROL_COCINA`) puede registrar entradas y salidas de productos en el inventario y ver los pedidos.