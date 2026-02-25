package org.example.mapper;

import org.example.modelo.dto.CompraDTO;
import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.entidad.CompraEntidad;

public class CompraMapper {

    public static CompraDTO paraDTO(CompraEntidad entidad) {
        if (entidad == null) return null;

        return new CompraDTO(
                entidad.getId(),
                null,
                null,
                entidad.getFechaCompra(),
                entidad.getMetodoPago(),
                entidad.getPrecioSinDescuento(),
                entidad.getDescuentoAplicado(),
                entidad.getEstadoCompra()
        );
    }
}
