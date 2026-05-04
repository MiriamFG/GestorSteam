package org.miriam.modelo.form;

import org.miriam.excepciones.FormularioInvalidoException;
import org.miriam.modelo.enums.EstadoCompra;
import org.miriam.modelo.enums.MetodoPago;

import java.time.LocalDate;
import java.util.ArrayList;

public class CompraForm {
    private Long idUsuario;
    private Long idJuego;
    private LocalDate fechaCompra;
    private MetodoPago metodoPago;
    private Double precioSinDescuento;
    private Integer descuentoAplicado;
    private EstadoCompra estadoCompra;

    public CompraForm(Long idUsuario, Long idJuego, LocalDate fechaCompra, MetodoPago metodoPago, Double precioSinDescuento, Integer descuentoAplicado, EstadoCompra estadoCompra) {
        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.fechaCompra = fechaCompra;
        this.metodoPago = metodoPago;
        this.precioSinDescuento = precioSinDescuento;
        this.descuentoAplicado = descuentoAplicado;
        this.estadoCompra = estadoCompra;
    }


    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdJuego() {
        return idJuego;
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

    /**
     * Valida los datos del formulario de compra antes de procesar la operación
     * <p>
     * Valida:
     * idusuario: obligatorio
     * idjuego: obligatorio
     * metodoPago: obligatorio
     * precioSinDescuento: obligatorio, no menor que 0 y debe tener maximo 2 decimales
     *
     * @throws FormularioInvalidoException si uno o más campos no cumplen las reglas de validación
     */
    public void validarForumulario() throws FormularioInvalidoException {
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorDTO("usuario", ErrorTipo.REQUERIDO));
        }

        if (idJuego == null) {
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
