package org.example.modelo.form;

import org.example.excepciones.FormularioInvalidoException;
import org.example.modelo.enums.ClasificacionEdad;
import org.example.modelo.enums.EstadoJuego;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JuegoForm {
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


    public JuegoForm(String titulo, String descipcion, String desarrollador, LocalDate fechaLanz, Double precioBase, Integer descuentoActual, String categoria, ClasificacionEdad clasificacionEdad, List<String> idiomasDisponibles, EstadoJuego estadoJuego) {
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
        List<ErrorDTO> errores = new ArrayList<>();

        if(titulo == null || titulo.trim().isEmpty()){
            errores.add(new ErrorDTO("titulo", ErrorTipo.REQUERIDO));
        }else{
            if(titulo.length() < 1 || titulo.length() > 100){
                errores.add(new ErrorDTO("titulo", ErrorTipo.LONGITUD_INVALIDA, 1, 100));
            }
        }

        if(descipcion != null && descipcion.length() > 200){
            errores.add(new ErrorDTO("descripcion", ErrorTipo.CAMPO_LARGO, 200));
        }

        if(desarrollador == null || desarrollador.trim().isEmpty()){
            errores.add(new ErrorDTO("nombreDesarrollador", ErrorTipo.REQUERIDO));
        }else{
            if(desarrollador.length() < 2 || desarrollador.length() > 100){
                errores.add(new ErrorDTO("nombreDesarrollador", ErrorTipo.LONGITUD_INVALIDA, 2, 100));
            }
        }

        if(fechaLanz == null){
            errores.add(new ErrorDTO("fechaLanzamiento", ErrorTipo.REQUERIDO));
        }else{

        }

        if(precioBase == null){
            errores.add(new ErrorDTO("precioBase", ErrorTipo.REQUERIDO));
        }else if(precioBase <0 || precioBase > 999.99){
            errores.add(new ErrorDTO("precioBase", ErrorTipo.CAMPO_ENTRE, 0.00, 999.99));
        }else{
            if(Math.round(precioBase *100)/ 100 != precioBase){
                errores.add(new ErrorDTO("precioBase", ErrorTipo.PRECIO_DECIMALES));
            }
        }

        if(descuentoActual == null){
            descuentoActual = 0;
        }else{
            if(descuentoActual < 0 || descuentoActual > 100){
                errores.add(new ErrorDTO("descuento", ErrorTipo.LONGITUD_INVALIDA, 0, 100));
            }
        }

        if(clasificacionEdad == null)
            errores.add(new ErrorDTO("clasificacionEdad", ErrorTipo.REQUERIDO));

        if(idiomasDisponibles != null || idiomasDisponibles.isEmpty()){
                errores.add(new ErrorDTO("idioma", ErrorTipo.REQUERIDO));
            }
            for (String idioma : idiomasDisponibles){
                if(idioma.length() > 200){
                    errores.add(new ErrorDTO("idioma", ErrorTipo.CAMPO_LARGO, 200));
                }
            }

        if(estadoJuego == null){
            estadoJuego = EstadoJuego.DISPONIBLE;
        }else {
            throw new FormularioInvalidoException(errores);
        }

    }

}


