package org.example.mapper;

import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.entidad.UsuarioEntidad;

public class UsuarioMapper {

    public static UsuarioDTO paraDTO(UsuarioEntidad usuario){
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getPais(),
                usuario.getFechaNac(),
                usuario.getFechaReg(),
                usuario.getAvatar(),
                usuario.getSaldoCartera(),
                usuario.getEstadoCuenta()
        );
    }
}
