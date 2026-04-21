package org.miriam.mapper;

import org.miriam.modelo.dto.CompraDTO;
import org.miriam.modelo.entidad.CompraEntidad;

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
