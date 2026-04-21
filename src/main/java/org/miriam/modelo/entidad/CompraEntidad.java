package org.miriam.modelo.entidad;

import org.miriam.modelo.enums.EstadoCompra;
import org.miriam.modelo.enums.MetodoPago;

import java.time.LocalDate;

public class CompraEntidad {

    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private LocalDate fechaCompra;
    private MetodoPago metodoPago;
    private Double precioSinDescuento;
    private Integer descuentoAplicado;
    private EstadoCompra estadoCompra;

    public CompraEntidad(Long id, Long usuarioId, Long juegoId, LocalDate fechaCompra, MetodoPago metodoPago, Double precioSinDescuento, EstadoCompra estadoCompra) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.juegoId = juegoId;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioSinDescuento = precioSinDescuento;
        this.descuentoAplicado = 0;
        this.estadoCompra = estadoCompra;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getJuegoId() {
        return juegoId;
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
