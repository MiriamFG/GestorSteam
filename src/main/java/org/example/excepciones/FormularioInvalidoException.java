package org.example.excepciones;

import org.example.modelo.form.ErrorDTO;

import java.util.ArrayList;
import java.util.List;

public class FormularioInvalidoException extends Exception {
    private List<String> errores;

    public FormularioInvalidoException(ArrayList<ErrorDTO> errores) {
        super("Errores de validación en el formulario");
        this.errores = errores;
    }
    public List<String> getErrores() {
        return errores;
    }
}
