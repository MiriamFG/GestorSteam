package org.miriam.modelo.entidad;

import org.miriam.modelo.enums.EstadoResena;

import java.time.LocalDate;

public class ResenaEntidad {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private Boolean recomendado;
    private String textoResena;
    private Double horasJuegoResena;
    private LocalDate fechaPubli;
    private LocalDate fechaUltimaEdicion;
    private EstadoResena estadoResena;

    public ResenaEntidad(Long id, Long usuarioId, Long juegoId, Boolean recomendado, String textoResena, Double horasJuegoResena, LocalDate fechaPubli, LocalDate fechaUltimaEdicion, EstadoResena estadoResena) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.juegoId = juegoId;
        this.recomendado = recomendado;
        this.textoResena = textoResena;
        this.horasJuegoResena = horasJuegoResena;
        this.fechaPubli = fechaPubli;
        this.fechaUltimaEdicion = fechaUltimaEdicion;
        this.estadoResena = estadoResena;
    }


    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getJuegoId() {
        return juegoId;
    }

    public Boolean getRecomendado() {
        return recomendado;
    }

    public String getTextoResena() {
        return textoResena;
    }

    public Double getHorasJuegoResena() {
        return horasJuegoResena;
    }

    public LocalDate getFechaPubli() {
        return fechaPubli;
    }

    public LocalDate getFechaUltimaEdicion() {
        return fechaUltimaEdicion;
    }

    public EstadoResena getEstadoResena() {
        return estadoResena;
    }
}
