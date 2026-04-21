package org.miriam.modelo.form;

import org.miriam.excepciones.FormularioInvalidoException;
import org.miriam.modelo.enums.ClasificacionEdad;
import org.miriam.modelo.enums.EstadoJuego;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JuegoForm {
    public static final int CIEN = 100;
    public static final int LONGITUD_1 = 1;
    public static final int LONGITUD_100 = 100;
    public static final int LONGITUD_2000 = 2000;
    public static final int LONGITUD_2 = 2;
    public static final double PRECIO_MINIMO = 0.00;
    public static final double MAX_PRECIO = 999.99;
    public static final int MIN_DESCUENTO = 0;
    public static final int MAX_DESCUENTO = 100;
    public static final int LONGITUD_IDIOMAS = 200;
    public static final int LONGITUD_0 = 0;
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

    /**
     * Valida los datos del formulario de juego
     * <p>
     * Valida:
     * titulo: oblitatorio y longitud entere 1 y 100 caracteres
     * descripcion: opcional y máximo 200 caracteres
     * desarrollador: obligaotrio y longitud entre 2 y 100 caractgeres
     * fechaLanzamiento: obligatorio
     * precioBase: obligatorio, debe estar entre 0.00 y 000.000, maximo dos decimales
     * descuentoActual: opcional(si es null se establece 0), debe estar entre 0 y 100
     * clasificacionEdad: obligatoria
     * idiomasDisponibles: obligatorio, cada idioma no puede superar 200 caracteres
     * estadoJuego: si es null se establece por defecto DISPONIBLE
     *
     * @throws FormularioInvalidoException si uno o más campos no cumplen las reglas de validación
     */
    public void validarForumulario() throws FormularioInvalidoException {
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        if (titulo == null || titulo.trim().isEmpty()) {
            errores.add(new ErrorDTO("titulo", ErrorTipo.REQUERIDO));
        } else {
            if (titulo.length() < LONGITUD_1 || titulo.length() > LONGITUD_100) {
                errores.add(new ErrorDTO("titulo", ErrorTipo.LONGITUD_INVALIDA, LONGITUD_1, LONGITUD_100));
            }
        }

        if (descipcion != null && descipcion.length() > LONGITUD_2000) {
            errores.add(new ErrorDTO("descripcion", ErrorTipo.CAMPO_LARGO, LONGITUD_2000));
        }

        if (desarrollador == null || desarrollador.trim().isEmpty()) {
            errores.add(new ErrorDTO("nombreDesarrollador", ErrorTipo.REQUERIDO));
        } else {
            if (desarrollador.length() < LONGITUD_2 || desarrollador.length() > LONGITUD_100) {
                errores.add(new ErrorDTO("nombreDesarrollador", ErrorTipo.LONGITUD_INVALIDA, LONGITUD_2, JuegoForm.LONGITUD_100));
            }
        }

        if (fechaLanz == null) {
            errores.add(new ErrorDTO("fechaLanzamiento", ErrorTipo.REQUERIDO));
        }

        if (precioBase == null) {
            errores.add(new ErrorDTO("precioBase", ErrorTipo.REQUERIDO));
        } else if (precioBase < PRECIO_MINIMO || precioBase > MAX_PRECIO) {
            errores.add(new ErrorDTO("precioBase", ErrorTipo.CAMPO_ENTRE, PRECIO_MINIMO, MAX_PRECIO));
        }

        var value =  new BigDecimal(String.valueOf(precioBase));
        if(value.stripTrailingZeros().scale() > LONGITUD_2)
            errores.add(new ErrorDTO("Precio Base", ErrorTipo.MAX_DECIMALES));


        if (descuentoActual == null) {
            descuentoActual = 0;
        } else {
            if (descuentoActual < LONGITUD_0 || descuentoActual > LONGITUD_100) {
                errores.add(new ErrorDTO("descuento", ErrorTipo.LONGITUD_INVALIDA, MIN_DESCUENTO, MAX_DESCUENTO));
            }
        }

        if (clasificacionEdad == null) {
            errores.add(new ErrorDTO("clasificacionEdad", ErrorTipo.REQUERIDO));
        }

        if (idiomasDisponibles == null || idiomasDisponibles.isEmpty()) {
            errores.add(new ErrorDTO("idioma", ErrorTipo.REQUERIDO));
        }
        for (String idioma : idiomasDisponibles) {
            if (idioma.length() > LONGITUD_IDIOMAS) {
                errores.add(new ErrorDTO("idioma", ErrorTipo.CAMPO_LARGO, LONGITUD_IDIOMAS));
            }
        }

        if (estadoJuego == null) {
            estadoJuego = EstadoJuego.DISPONIBLE;
        }

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException(errores);
        }
    }
}


