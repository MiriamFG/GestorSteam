package org.example.mapper;

import org.example.modelo.dto.BibliotecaDTO;
import org.example.modelo.entidad.BibliotecaEntidad;

public class BibliotecaMapper {

    public static BibliotecaDTO paraDTO(BibliotecaEntidad biblioteca){
        return new BibliotecaDTO(
                biblioteca.getId(),
                biblioteca.getUsuarioId(),
                biblioteca.getJuegoId(),
                biblioteca.getFechaAdquisicion(),
                biblioteca.getNumHorasTotal(),
                biblioteca.getUltimaFechaJuego(),
                biblioteca.getEstadoInstalacion()
        );
    }

}
