package org.example.mapper;

import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.entidad.JuegoEntidad;

public class JuegoMapper {

    public static JuegoDTO paraDTO(JuegoEntidad juego){
        return new JuegoDTO(
                juego.getId(),
                juego.getTitulo(),
                juego.getDescipcion(),
                juego.getDesarrollador(),
                juego.getFechaLanz(),
                juego.getPrecioBase(),
                juego.getDescuentoActual(),
                juego.getCategoria(),
                juego.getClasificacionEdad(),
                juego.getIdiomasDisponibles(),
                juego.getEstadoJuego()
        );
    }
}
