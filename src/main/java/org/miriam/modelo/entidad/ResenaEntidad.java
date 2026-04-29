package org.miriam.modelo.entidad;

import jakarta.persistence.*;
import org.miriam.modelo.enums.EstadoResena;

import java.time.LocalDate;
@Table(name = "resena")
@Entity

public class ResenaEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id")
    private Long usuarioId;
    @Column(name = "juego_id")
    private Long juegoId;
    @Column(name = "recomendado")
    private Boolean recomendado;
    @Column(name = "texto_resena")
    private String textoResena;
    @Column(name = "horas_juego_resena")
    private Double horasJuegoResena;
    @Column(name = "fecha_publi")
    private LocalDate fechaPubli;
    @Column(name = "ultima_edicion")
    private LocalDate fechaUltimaEdicion;
    @Column(name = "estado_resena")
    private EstadoResena estadoResena;

    public ResenaEntidad() {

    }

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
