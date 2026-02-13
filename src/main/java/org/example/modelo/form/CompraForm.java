package org.example.modelo.form;

import org.example.excepciones.FormularioInvalidoException;
import org.example.modelo.enums.EstadoCompra;
import org.example.modelo.enums.MetodoPago;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompraForm {
    private Long usuarioDTO;
    private Long juegoDTO;
    private LocalDate fechaCompra;
    private MetodoPago metodoPago;
    private Double precioSinDescuento;
    private EstadoCompra estadoCompra;

    public CompraForm(Long usuarioDTO, Long juegoDTO, LocalDate fechaCompra, MetodoPago metodoPago, Double precioSinDescuento, Integer descuentoAplicado, EstadoCompra estadoCompra) {
        this.usuarioDTO = usuarioDTO;
        this.juegoDTO = juegoDTO;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioSinDescuento = precioSinDescuento;
        this.estadoCompra = estadoCompra;
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



    public EstadoCompra getEstadoCompra() {
        return estadoCompra;
    }

    public void validarForumulario() throws FormularioInvalidoException {
        List<ErrorDTO> errores = new ArrayList<>();

}
