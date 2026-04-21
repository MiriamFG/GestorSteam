package org.miriam.mapper;

import org.miriam.modelo.dto.UsuarioDTO;
import org.miriam.modelo.entidad.UsuarioEntidad;

public class UsuarioMapper {

    public static UsuarioDTO paraDTO(UsuarioEntidad usuario) {
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
