package org.miriam.excepciones;

import org.miriam.modelo.form.ErrorDto;

import java.util.ArrayList;
import java.util.List;
public class FormularioInvalidoException extends Exception {

    private List<ErrorDto> errores;

    public FormularioInvalidoException(ArrayList<ErrorDto> errores) {
        super("Errores de validación en el formulario");
        this.errores = errores;
    }

    public List<ErrorDto> getErrores() {
        return errores;
    }
}
