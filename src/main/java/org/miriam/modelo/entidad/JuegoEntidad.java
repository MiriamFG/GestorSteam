package org.miriam.modelo.entidad;

import jakarta.persistence.*;
import org.miriam.HibernateUtil;
import org.miriam.modelo.enums.ClasificacionEdad;
import org.miriam.modelo.enums.EstadoJuego;

import java.time.LocalDate;
import java.util.List;
@Table(name = "juegos")
@Entity
public class JuegoEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "descipcion")
    private String descipcion;
    @Column(name = "desarrollador")
    private String desarrollador;
    @Column(name = "fechaLanz")
    private LocalDate fechaLanz;
    @Column(name = "precio_base")
    private Double precioBase;
    @Column(name = "descuento_actual")
    private Integer descuentoActual;
    @Column(name = "categoria")
    private String categoria;
    @Column(name = "clasificacion_edad")
    private ClasificacionEdad clasificacionEdad;
    @Column(name = "idiomas")
    private List<String> idiomasDisponibles;
    @Column(name = "estado")
    private EstadoJuego estadoJuego;


    public JuegoEntidad(Long id, String titulo, String descipcion, String desarrollador, LocalDate fechaLanz, Double precioBase, Integer descuentoActual, String categoria, ClasificacionEdad clasificacionEdad, List<String> idiomasDisponibles, EstadoJuego estadoJuego) {
        this.id = id;
        this.titulo = titulo;
        this.descipcion = descipcion;
        this.desarrollador = desarrollador;
        this.fechaLanz = fechaLanz;
        this.precioBase = precioBase;
        this.descuentoActual = descuentoActual;
        this.categoria = categoria;
        this.clasificacionEdad = clasificacionEdad;
        this.idiomasDisponibles = idiomasDisponibles;
        this.estadoJuego = estadoJuego;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescipcion() {
        return descipcion;
    }

    public String getDesarrollador() {
        return desarrollador;
    }

    public LocalDate getFechaLanz() {
        return fechaLanz;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public Integer getDescuentoActual() {
        return descuentoActual;
    }

    public String getCategoria() {
        return categoria;
    }

    public ClasificacionEdad getClasificacionEdad() {
        return clasificacionEdad;
    }

    public List<String> getIdiomasDisponibles() {
        return idiomasDisponibles;
    }

    public EstadoJuego getEstadoJuego() {
        return estadoJuego;
    }

    public void setDescuentoActual(Integer descuentoActual) {
        this.descuentoActual = descuentoActual;
    }

    public void setEstadoJuego(EstadoJuego estadoJuego) {
        this.estadoJuego = estadoJuego;
    }


    public static void main(String[] args) {
        var session = HibernateUtil.getSessionFactory().openSession();
        session.close();

    }
}


