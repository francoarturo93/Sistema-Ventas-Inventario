package com.back.inventario.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.format.DateTimeFormatter;

import com.back.inventario.dto.RegistroSalidaResponseDTO;
import com.back.inventario.entities.RegistroSalida;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RegistroSalidaMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 'a las' HH:mm");

    @Mapping(source = "id", target = "id")
    @Mapping(source = "producto.id", target = "productoId") // Mapeo del ID del producto
    @Mapping(source = "producto.nombre", target = "nombre") // Mapeo del ID del producto
    @Mapping(source = "cantidadSalida", target = "cantidadSalida")
    @Mapping(target = "fechaSalida", expression = "java(registroSalida.getFechaSalida().format(formatter))") // Formatea la fecha
    RegistroSalidaResponseDTO registroSalidaToResponseDTO(RegistroSalida registroSalida);
}
