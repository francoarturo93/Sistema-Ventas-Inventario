package com.back.inventario.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.back.inventario.dto.ProductoDTO;
import com.back.inventario.entities.Producto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProductoMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "entradas", target = "entradas")
    @Mapping(source = "categoria", target = "categoria")
    Producto productoDTOToProducto(ProductoDTO productoDTO);
}
