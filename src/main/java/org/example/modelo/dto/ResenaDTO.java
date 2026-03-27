package org.example.modelo.dto;

import org.example.modelo.enums.EstadoResena;

import java.time.LocalDate;

public class ResenaDTO {

    private Long id;
    private UsuarioDTO usuarioDTO;
    private JuegoDTO juegoDTO;
    private Boolean recomendado;
    private String textoResena;
    private Double horasJuegoResena;
    private LocalDate fechaPubli;
    private LocalDate fechaUltimaEdicion;
    private EstadoResena estadoResena;

    public ResenaDTO(Long id, UsuarioDTO usuarioDTO, JuegoDTO juegoDTO, Boolean recomendado, String textoResena, Double horasJuegoResena, LocalDate fechaPubli, LocalDate fechaUltimaEdicion, EstadoResena estadoResena) {
        this.id = id;
        this.usuarioDTO = usuarioDTO;
        this.juegoDTO = juegoDTO;
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

    public UsuarioDTO getUsuarioDTO() {
        return usuarioDTO;
    }

    public JuegoDTO getJuegoDTO() {
        return juegoDTO;
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
