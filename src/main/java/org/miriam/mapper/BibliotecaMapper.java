package org.miriam.mapper;

import org.miriam.modelo.dto.BibliotecaDTO;
import org.miriam.modelo.dto.JuegoDTO;
import org.miriam.modelo.dto.UsuarioDTO;
import org.miriam.modelo.entidad.BibliotecaEntidad;

public class BibliotecaMapper {

    public static BibliotecaDTO paraDTO(BibliotecaEntidad biblioteca, UsuarioDTO usuarioDTO, JuegoDTO juegoDTO) {
        return new BibliotecaDTO(
                biblioteca.getId(),
                usuarioDTO,
                biblioteca.getUsuarioId(),
                juegoDTO,
                biblioteca.getJuegoId(),
                biblioteca.getFechaAdquisicion(),
                biblioteca.getNumHorasTotal(),
                biblioteca.getUltimaFechaJuego(),
                biblioteca.getEstadoInstalacion()
        );
    }

}
