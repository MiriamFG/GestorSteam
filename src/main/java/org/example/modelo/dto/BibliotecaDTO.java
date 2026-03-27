package org.example.modelo.dto;

import org.example.modelo.entidad.BibliotecaEntidad;
import org.example.modelo.enums.EstadoInstalacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BibliotecaDTO {
    private Long id;
    private UsuarioDTO usuarioDTO;
    private Long usuarioId;
    private JuegoDTO juegoDTO;
    private Long juegoId;
    private LocalDateTime fechaAdquisicion;
    private Integer numHorasTotal;
    private LocalDate ultimaFechaJuego;
    private EstadoInstalacion estadoInstalacion;

    public BibliotecaDTO(Long id, UsuarioDTO usuarioDTO, Long usuarioId, JuegoDTO juegoDTO, Long juegoId, LocalDateTime fechaAdquisicion, Integer numHorasTotal, LocalDate ultimaFechaJuego, EstadoInstalacion estadoInstalacion) {
        this.id = id;
        this.usuarioDTO = usuarioDTO;
        this.usuarioId = usuarioId;
        this.juegoDTO = juegoDTO;
        this.juegoId = juegoId;
        this.fechaAdquisicion = fechaAdquisicion;
        this.numHorasTotal = numHorasTotal;
        this.ultimaFechaJuego = ultimaFechaJuego;
        this.estadoInstalacion = estadoInstalacion;
    }

    public BibliotecaDTO(Long id, Long usuarioId, Long juegoId, LocalDateTime fechaAdquisicion, Integer numHorasTotal, LocalDate ultimaFechaJuego, EstadoInstalacion estadoInstalacion) {
    }

    public Long getId() {
        return id;
    }

    public UsuarioDTO getUsuarioDTO() {
        return usuarioDTO;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public JuegoDTO getJuegoDTO() {
        return juegoDTO;
    }

    public Long getJuegoId() {
        return juegoId;
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
