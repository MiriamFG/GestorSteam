package org.example.modelo.dto;

import org.example.modelo.enums.EstadoInstalacion;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BibliotecaDTO {
    private Long id;
    private UsuarioDTO usuarioDTO;
    private JuegoDTO juegoDTO;
    private LocalDateTime fechaAdquisicion;
    private Integer numHorasTotal;
    private LocalDate ultimaFechaJuego;
    private EstadoInstalacion estadoInstalacion;

    public BibliotecaDTO(Long id, UsuarioDTO usuarioDTO, JuegoDTO juegoDTO, LocalDateTime fechaAdquisicion, Integer numHorasTotal, LocalDate ultimaFechaJuego, EstadoInstalacion estadoInstalacion) {
        this.id = id;
        this.usuarioDTO = usuarioDTO;
        this.juegoDTO = juegoDTO;
        this.fechaAdquisicion = fechaAdquisicion;
        this.numHorasTotal = numHorasTotal;
        this.ultimaFechaJuego = ultimaFechaJuego;
        this.estadoInstalacion = estadoInstalacion;
    }

    public EstadoInstalacion getEstadoInstalacion() {
        return estadoInstalacion;
    }

    public LocalDate getUltimaFechaJuego() {
        return ultimaFechaJuego;
    }

    public Integer getNumHorasTotal() {
        return numHorasTotal;
    }

    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public JuegoDTO getJuegoDTO() {
        return juegoDTO;
    }

    public UsuarioDTO getUsuarioDTO() {
        return usuarioDTO;
    }

    public Long getId() {
        return id;
    }
}
