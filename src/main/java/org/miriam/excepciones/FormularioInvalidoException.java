package org.miriam.excepciones;

import org.miriam.modelo.form.ErrorDTO;

import java.util.ArrayList;
import java.util.List;

public class FormularioInvalidoException extends Exception {
    private List<ErrorDTO> errores;

    public FormularioInvalidoException(ArrayList<ErrorDTO> errores) {
        super("Errores de validación en el formulario");
        this.errores = errores;
    }

    public List<ErrorDTO> getErrores() {
        return errores;
    }
}
