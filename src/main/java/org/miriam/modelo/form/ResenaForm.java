package org.miriam.modelo.form;

import org.miriam.excepciones.FormularioInvalidoException;
import org.miriam.modelo.enums.EstadoResena;

import java.util.ArrayList;
import java.util.List;

public class ResenaForm {
    private Long idUsuario;
    private Long idJuego;
    private Boolean recomendado;
    private String textoResena;
    private Double horasJuegoResena;
    private EstadoResena estado;


    public ResenaForm(Long idUsuario, Long idJuego, Boolean recomendado, String textoResena, Double horas) {
        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.recomendado = recomendado;
        this.textoResena = textoResena;
        this.horasJuegoResena = horas;
        this.estado = EstadoResena.PUBLICADA;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdJuego() {
        return idJuego;
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

    //constructor 6 parámetros para formOcultar
    public ResenaForm(Long idUsuario, Long idJuego, Boolean recomendado, String texto, Double horas, EstadoResena estado) {
        this.idUsuario = idUsuario;
        this.idJuego = idJuego;
        this.recomendado = recomendado;
        this.textoResena = texto;
        this.horasJuegoResena = horas;
        this.estado = estado;
    }


    /**
     * Valida los datos del formulario de una reseña.
     * <p>
     * verifica:
     * Que el ID de usuario no sea nulo.
     * Que el ID de juego no sea nulo.
     * Que el campo 'recomendado' no sea nulo.
     * Que el texto de la reseña no sea nulo y tenga entre 50 y 8000 caracteres.</li>
     * <p>
     * Si algún campo no cumple las condiciones, se recopilan los errores en una lista
     * y se lanza {@link FormularioInvalidoException}.
     *
     * @throws FormularioInvalidoException si uno o más campos son inválidos
     */
    public void validarFormulario() throws FormularioInvalidoException {
        List<ErrorDTO> errores = new ArrayList<>();

        if (idUsuario == null) {
            errores.add(new ErrorDTO("usuario", ErrorTipo.REQUERIDO));
        }

        if (idJuego == null) {
            errores.add(new ErrorDTO("juego", ErrorTipo.REQUERIDO));
        }

        if (recomendado == null) {
            errores.add(new ErrorDTO("recomendado", ErrorTipo.REQUERIDO));
        }

        if (textoResena == null || textoResena.trim().isEmpty()) {
            errores.add(new ErrorDTO("texto", ErrorTipo.REQUERIDO));
        } else if (textoResena.length() < 50 || textoResena.length() > 8000) {
            errores.add(new ErrorDTO("texto", ErrorTipo.LONGITUD_INVALIDA, 50, 8000));
        }

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException((ArrayList<ErrorDTO>) errores);
        }
    }

}
