package org.example.modelo.entidad;

import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.enums.EstadoInstalacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BibliotecaEntidad {

    private Long id;
    private Long usuarioDTO;
    private Long juegoDTO;
    private LocalDateTime fechaAdquisicion;
    private Integer numHorasTotal;
    private LocalDate ultimaFechaJuego;
    private EstadoInstalacion estadoInstalacion;

    public BibliotecaEntidad(Long id, Long usuarioDTO, Long juegoDTO, LocalDateTime fechaAdquisicion, Integer numHorasTotal, LocalDate ultimaFechaJuego, EstadoInstalacion estadoInstalacion) {
        this.id = id;
        this.usuarioDTO = usuarioDTO;
        this.juegoDTO = juegoDTO;
        this.fechaAdquisicion = fechaAdquisicion;
        this.numHorasTotal = numHorasTotal;
        this.ultimaFechaJuego = ultimaFechaJuego;
        this.estadoInstalacion = estadoInstalacion;
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
}
