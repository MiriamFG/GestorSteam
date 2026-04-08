package org.example.modelo.form;

import org.example.excepciones.FormularioInvalidoException;
import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.enums.EstadoInstalacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaForm {

    private Long idUsuario;
    private Long idJuego;
    private LocalDateTime fechaAdquisicion;
    private Double numHorasTotal;
    private LocalDateTime ultimaFechaJuego;
    private EstadoInstalacion estadoInstalacion;

    public BibliotecaForm(Long idUsuario, Long idJuego, LocalDateTime fechaAdquisicion, Double numHorasTotal, LocalDateTime ultimaFechaJuego, EstadoInstalacion estadoInstalacion) {
        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.fechaAdquisicion = fechaAdquisicion;
        this.numHorasTotal = numHorasTotal;
        this.ultimaFechaJuego = ultimaFechaJuego;
        this.estadoInstalacion = estadoInstalacion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdJuego() {
        return idJuego;
    }

    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public Double getNumHorasTotal() {
        return numHorasTotal;
    }

    public LocalDateTime getUltimaFechaJuego() {
        return ultimaFechaJuego;
    }

    public EstadoInstalacion getEstadoInstalacion() {
        return estadoInstalacion;
    }

    /**
     * Valida los datos del formulario de adquisición o registro de juego por parte de un usuario
     * <p>
     * Valida:
     * idUsuario: obligatorio
     * idJuego: obligatorio
     * fechaAdquisicion: obligatorio, no puede ser fecha futura
     * numHorasTotal: no menor que 0 y debe tener máximo 1 decimal
     * ultimaFechaJuego: no peude ser fecha futura, no puede ser anterior a la fechaAdquisicion
     * estadoInstalacion: si es null y existe ultimaFechaJuego, se establece por defecto NO_INSTALADO
     *
     * @throws FormularioInvalidoException si los datos del formulario no cumplen las reglas de validación
     */
    public void validarForumulario() throws FormularioInvalidoException {
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorDTO("usuario", ErrorTipo.REQUERIDO));
        }

        if (idJuego == null) {
            errores.add(new ErrorDTO("juego", ErrorTipo.REQUERIDO));
        }

        if (fechaAdquisicion == null) {
            errores.add(new ErrorDTO("fecha", ErrorTipo.FECHA_OBLIGATORIA));
        }

        if (fechaAdquisicion.isAfter(LocalDateTime.now())) {
            errores.add(new ErrorDTO("fecha", ErrorTipo.FECHA_FUTURA));
        }
        if (numHorasTotal != null) {
            if (numHorasTotal < 0) {
                errores.add(new ErrorDTO("numHorasTotal", ErrorTipo.VALOR_DEMASIADO_BAJO));
            }
            double valorPorDiez = numHorasTotal * 10;
            if (Math.abs(valorPorDiez - Math.round(valorPorDiez)) > 0.001) {
                errores.add(new ErrorDTO("numHorasTotal", ErrorTipo.FORMATO_INVALIDO));
            }
        }


        if (ultimaFechaJuego != null) {
            if (ultimaFechaJuego.isAfter(LocalDateTime.now())) {
                errores.add(new ErrorDTO("ultimaFechaJuego", ErrorTipo.FECHA_FUTURA));
            }
            if (fechaAdquisicion != null && ultimaFechaJuego.isBefore(fechaAdquisicion)) {
                errores.add(new ErrorDTO("ultimaFechaJuego", ErrorTipo.VALOR_DEMASIADO_BAJO));
            }

            if (estadoInstalacion == null) {
                estadoInstalacion = EstadoInstalacion.NO_INSTALADO;
            }
        }
        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException(errores);
        }
    }
}
