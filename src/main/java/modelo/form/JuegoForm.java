package modelo.form;

import excepciones.FormularioInvalidoException;
import modelo.enums.ClasificacionEdad;
import modelo.enums.EstadoJuego;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JuegoForm {
    private Long id;
    private String titulo;
    private String descipcion;
    private String desarrollador;
    private LocalDate fechaLanz;
    private Double precioBase;
    private Integer descuentoActual;
    private String categoria;
    private ClasificacionEdad clasificacionEdad;
    private List<String> idiomasDisponibles;
    private EstadoJuego estadoJuego;


    public JuegoForm(Long id, String titulo, String descipcion, String desarrollador, LocalDate fechaLanz, Double precioBase, Integer descuentoActual, String categoria, ClasificacionEdad clasificacionEdad, List<String> idiomasDisponibles, EstadoJuego estadoJuego) {
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

    public void validarForumulario() throws FormularioInvalidoException {
        List<String> errores = new ArrayList<>();

        if(titulo == null || titulo.trim().isEmpty()){
            errores.add("El titulo del juego es obligatorio");
        }else{
            if(titulo.length() < 1 || titulo.length() > 100){
                errores.add("El titulo debe tener entre 1 y 100 caracteres");
            }
        }

        if(descipcion != null && descipcion.length() > 200){
            errores.add("La descripcion no puede superar los 200 caracteres");
        }

        if(desarrollador == null || desarrollador.trim().isEmpty()){
            errores.add("El nombre del desarrollador es obligatorio");
        }else{
            if(desarrollador.length() < 2 || desarrollador.length() > 100){
                errores.add("El nombre del desarrollador debe tener entre 2 y 100 caracteres");
            }
        }

        if(fechaLanz == null){
            errores.add("La fecha de lanzamiento es obligatoria");
        }else{

        }

        if(precioBase == null){
            errores.add("El precio base es obligatorio");
        }else if(precioBase <0 || precioBase > 999.99){
            errores.add("El precio base debe estar entre 0,00 y 999,99");
        }else{
            if(Math.round(precioBase *100)/ 100 != precioBase){
                errores.add("El precio base no puede tener más de 2 decimales");
            }
        }

        if(descuentoActual == null){
            descuentoActual = 0;
        }else{
            if(descuentoActual < 0 || descuentoActual > 100){
                errores.add("el descuento debe estar entre 0 y 100");
            }
        }

        if(clasificacionEdad == null)
            errores.add("La clasificación por edad es obligatoria");

        if(idiomasDisponibles != null || idiomasDisponibles.isEmpty()){
                errores.add("Debe hacer al menos un idioma");
            }
            for (String idioma : idiomasDisponibles){
                if(idioma.length() > 200){
                    errores.add("Cada idioma no puede superar los 200 caracteres");
                }
            }

        if(estadoJuego == null){
            estadoJuego = EstadoJuego.DISPONIBLE;
        }

    }

}


