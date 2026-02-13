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
    private Integer numHorasTotal;
    private LocalDate ultimaFechaJuego;
    private EstadoInstalacion estadoInstalacion;

    public BibliotecaForm(Long idUsuario, Long idJuego, LocalDateTime fechaAdquisicion, Integer numHorasTotal, LocalDate ultimaFechaJuego, EstadoInstalacion estadoInstalacion) {
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

    public Integer getNumHorasTotal() {
        return numHorasTotal;
    }

    public LocalDate getUltimaFechaJuego() {
        return ultimaFechaJuego;
    }

    public EstadoInstalacion getEstadoInstalacion() {
        return estadoInstalacion;
    }

    public void validarForumulario() throws FormularioInvalidoException {
        List<ErrorDTO> errores = new ArrayList<>();

        if(idUsuario == null){
            errores.add("El usuario es obligatorio");
        }

        if(idJuego == null){
            errores.add("El juego es obligatorio");
        }

        if(fechaAdquisicion == null){
            errores.add((new ErrorDTO("fecha", ErrorTipo.FECHA_OBLIGATORIA));
        }

        if(fechaAdquisicion.isAfter(LocalDateTime.now())){
            errores.add(new ErrorDTO("fecha", ErrorTipo.FECHA_FUTURA));
        }
    }

}
