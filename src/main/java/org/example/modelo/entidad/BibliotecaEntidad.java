package org.example.modelo.entidad;

import org.example.modelo.enums.EstadoInstalacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BibliotecaEntidad {

    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private LocalDateTime fechaAdquisicion;
    private Integer numHorasTotal;
    private LocalDateTime ultimaFechaJuego;
    private EstadoInstalacion estadoInstalacion;

    public BibliotecaEntidad(Long id, Long usuarioId, Long juegoId, LocalDateTime fechaAdquisicion, Integer numHorasTotal, LocalDateTime ultimaFechaJuego, EstadoInstalacion estadoInstalacion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.juegoId = juegoId;
        this.fechaAdquisicion = fechaAdquisicion;
        this.numHorasTotal = numHorasTotal;
        this.ultimaFechaJuego = ultimaFechaJuego;
        this.estadoInstalacion = estadoInstalacion;
    }

    public EstadoInstalacion getEstadoInstalacion() {
        return estadoInstalacion;
    }

    public LocalDateTime getUltimaFechaJuego() {
        return ultimaFechaJuego;
    }

    public Integer getNumHorasTotal() {
        return numHorasTotal;
    }

    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public Long getJuegoId() {
        return juegoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getId() {
        return id;
    }
}
