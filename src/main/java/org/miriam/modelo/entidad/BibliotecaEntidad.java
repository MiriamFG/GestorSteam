package org.miriam.modelo.entidad;

import org.miriam.modelo.enums.EstadoInstalacion;

import java.time.LocalDateTime;

public class BibliotecaEntidad {

    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private LocalDateTime fechaAdquisicion;
    private Double numHorasTotal;
    private LocalDateTime ultimaFechaJuego;
    private EstadoInstalacion estadoInstalacion;

    public BibliotecaEntidad(Long id, Long usuarioId, Long juegoId, LocalDateTime fechaAdquisicion, Double numHorasTotal, LocalDateTime ultimaFechaJuego, EstadoInstalacion estadoInstalacion) {
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

    public Double getNumHorasTotal() {
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
