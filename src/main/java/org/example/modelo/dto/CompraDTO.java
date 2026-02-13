package org.example.modelo.dto;

import org.example.modelo.enums.EstadoCompra;
import org.example.modelo.enums.MetodoPago;

import java.time.LocalDate;

public class CompraDTO {
    private Long id;
    private UsuarioDTO usuarioDTO;
    private JuegoDTO juegoDTO;
    private LocalDate fechaCompra;
    private MetodoPago metodoPago;
    private Double precioSinDescuento;
    private Integer descuentoAplicado;
    private EstadoCompra estadoCompra;

    public CompraDTO(Long id, UsuarioDTO usuarioDTO, JuegoDTO juegoDTO, LocalDate fechaCompra, MetodoPago metodoPago, Double precioSinDescuento, Integer descuentoAplicado, EstadoCompra estadoCompra) {
        this.id = id;
        this.usuarioDTO = usuarioDTO;
        this.juegoDTO = juegoDTO;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioSinDescuento = precioSinDescuento;
        this.descuentoAplicado = descuentoAplicado;
        this.estadoCompra = estadoCompra;
    }

    public Long getId() {
        return id;
    }

    public UsuarioDTO getUsuarioDTO() {
        return usuarioDTO;
    }

    public JuegoDTO getJuegoDTO() {
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
