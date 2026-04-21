package org.miriam.mapper;

import org.miriam.modelo.dto.JuegoDTO;
import org.miriam.modelo.dto.ResenaDTO;
import org.miriam.modelo.dto.UsuarioDTO;
import org.miriam.modelo.entidad.ResenaEntidad;

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
