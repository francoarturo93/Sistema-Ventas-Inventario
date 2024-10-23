package com.back.inventario.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.format.DateTimeFormatter;

import com.back.inventario.dto.RegistroEntradaResponseDTO;
import com.back.inventario.entities.RegistroEntrada;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RegistroEntradaMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 'a las' HH:mm");

    @Mapping(source = "id", target = "id")
    @Mapping(source = "producto.id", target = "productoId") // Mapeo del ID del producto
    @Mapping(source = "producto.nombre", target = "nombre") // Mapeo del ID del producto
    @Mapping(source = "cantidadEntrada", target = "cantidadEntrada")
    @Mapping(target = "fechaEntrada", expression = "java(registroEntrada.getFechaEntrada().format(formatter))") // Formatea la fecha
    RegistroEntradaResponseDTO registroEntradaToResponseDTO(RegistroEntrada registroEntrada);
    
}