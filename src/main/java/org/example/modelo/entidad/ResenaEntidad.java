package org.example.modelo.entidad;

import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.enums.EstadoResena;

import java.time.LocalDate;

public class ResenaEntidad {
    private Long id;
    private Long usuarioDTO;
    private Long juegoDTO;
    private Boolean recomendado;
    private String textoResena;
    private Double horasJuegoResena;
    private LocalDate fechaPubli;
    private LocalDate fechaUltimaEdicion;
    private EstadoResena estadoResena;

    public ResenaEntidad(Long id, Long usuarioDTO, Long juegoDTO, Boolean recomendado, String textoResena, Double horasJuegoResena, LocalDate fechaPubli, LocalDate fechaUltimaEdicion, EstadoResena estadoResena) {
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

    public ResenaEntidad(Long id, Long idUsuario, Long idJuego, Boolean recomendado, String textoResena) {
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
