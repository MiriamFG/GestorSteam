package org.example.mapper;

import org.example.modelo.dto.ResenaDTO;
import org.example.modelo.entidad.ResenaEntidad;

public class ResenaMapper {

    public static ResenaDTO paraDTO (ResenaEntidad resena){
        return new ResenaDTO(
                resena.getId(),
                resena.getUsuarioId(),
                resena.getJuegoId(),
                resena.getRecomendado(),
                resena.getTextoResena(),
                resena.getHorasJuegoResena(),
                resena.getFechaPubli(),
                resena.getFechaUltimaEdicion(),
                resena.getEstadoResena()
        );
    }
}
