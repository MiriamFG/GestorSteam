package org.miriam.modelo.entidad;

import jakarta.persistence.*;
import org.miriam.modelo.enums.EstadoInstalacion;

import java.time.LocalDateTime;
@Table(name = "biblioteca")
@Entity

public class BibliotecaEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id")
    private Long usuarioId;
    @Column(name = "juego_id")
    private Long juegoId;
    @Column(name = "fecha_adquisicion")
    private LocalDateTime fechaAdquisicion;
    @Column(name = "total_horas")
    private Double numHorasTotal;
    @Column(name = "fecha_juego")
    private LocalDateTime ultimaFechaJuego;
    @Column(name = "estado_intalacion")
    private EstadoInstalacion estadoInstalacion;

    public BibliotecaEntidad() {

    }

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
