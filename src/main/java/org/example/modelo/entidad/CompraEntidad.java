package org.example.modelo.entidad;

import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.enums.EstadoCompra;
import org.example.modelo.enums.MetodoPago;

import java.time.LocalDate;

public class CompraEntidad {

    private Long id;
    private Long usuarioDTO;
    private Long juegoDTO;
    private LocalDate fechaCompra;
    private MetodoPago metodoPago;
    private Double precioSinDescuento;
    private Integer descuentoAplicado;
    private EstadoCompra estadoCompra;

    public CompraEntidad(Long id, Long usuarioDTO, Long juegoDTO, LocalDate fechaCompra, MetodoPago metodoPago, Double precioSinDescuento, EstadoCompra estadoCompra) {
        this.id = id;
        this.usuarioDTO = usuarioDTO;
        this.juegoDTO = juegoDTO;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioSinDescuento = precioSinDescuento;
        this.descuentoAplicado = 0;
        this.estadoCompra = estadoCompra;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioDTO() {
        return usuarioDTO;
    }

    public Long getJuegoDTO() {
        return juegoDTO;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public Double getPrecioSinDescuento() {
        return precioSinDescuento;
    }

    public Integer getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public EstadoCompra getEstadoCompra() {
        return estadoCompra;
    }
}
