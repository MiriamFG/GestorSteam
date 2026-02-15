package org.example.modelo.form;

import org.example.excepciones.FormularioInvalidoException;
import org.example.modelo.enums.EstadoCompra;
import org.example.modelo.enums.MetodoPago;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompraForm {
    private Long idusuario;
    private Long idjuego;
    private LocalDate fechaCompra;
    private MetodoPago metodoPago;
    private Double precioSinDescuento;
    private EstadoCompra estadoCompra;

    public CompraForm(Long idusuario, Long idjuego, LocalDate fechaCompra, MetodoPago metodoPago, Double precioSinDescuento, Integer descuentoAplicado, EstadoCompra estadoCompra) {
        this.idusuario = idusuario;
        this.idjuego = idjuego;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioSinDescuento = precioSinDescuento;
        this.estadoCompra = estadoCompra;
    }


    public Long getUsuarioDTO() {
        return idusuario;
    }

    public Long getJuegoDTO() {
        return idjuego;
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

        if (idusuario == null) {
            errores.add(new ErrorDTO("usuario", ErrorTipo.REQUERIDO));
        }

        if (idjuego == null) {
            errores.add(new ErrorDTO("juego", ErrorTipo.REQUERIDO));
        }

        if (metodoPago == null) {
            errores.add(new ErrorDTO("metodoPago", ErrorTipo.REQUERIDO));
        }


        if (precioSinDescuento == null) {
            errores.add(new ErrorDTO("precioSinDescuento", ErrorTipo.REQUERIDO));
        } else {

            if (precioSinDescuento < 0) {
                errores.add(new ErrorDTO("precioSinDescuento", ErrorTipo.VALOR_DEMASIADO_BAJO));
            }

            if (Math.round(precioSinDescuento * 100) / 100.0 != precioSinDescuento) {
                errores.add(new ErrorDTO("precioSinDescuento", ErrorTipo.FORMATO_INVALIDO));
            }
        }

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException(errores);
        }

    }

}
