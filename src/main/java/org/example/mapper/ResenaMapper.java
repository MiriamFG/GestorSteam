package org.example.mapper;

import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.dto.ResenaDTO;
import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.entidad.ResenaEntidad;

public class ResenaMapper {

    public static ResenaDTO paraDTO(ResenaEntidad resena, UsuarioDTO usuario, JuegoDTO juego) {
        return new ResenaDTO(
                resena.getId(),
                usuario,
                juego,
                resena.getRecomendado(),
                resena.getTextoResena(),
                resena.getHorasJuegoResena(),
                resena.getFechaPubli(),
                resena.getFechaUltimaEdicion(),
                resena.getEstadoResena()
        );
    }
}
