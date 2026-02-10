package modelo.entidad;

import modelo.enums.ClasificacionEdad;
import modelo.enums.EstadoJuego;

import java.time.LocalDate;
import java.util.List;

public class JuegoEntidad {
    private Long id;
    private String titulo;
    private String descipcion;
    private String desarrollador;
    private LocalDate fechaLanz;
    private double precioBase;
    private int descuentoActual;
    private String categoria;
    private ClasificacionEdad clasificacionEdad;
    private List<String> idiomasDisponibles;
    private EstadoJuego estadoJuego;


    public JuegoEntidad(Long id, String titulo, String descipcion, String desarrollador, LocalDate fechaLanz, double precioBase, int descuentoActual, String categoria, ClasificacionEdad clasificacionEdad, List<String> idiomasDisponibles, EstadoJuego estadoJuego) {
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

    public String getTitulo(){
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

    public double getPrecioBase() {
        return precioBase;
    }

    public int getDescuentoActual() {
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
}
