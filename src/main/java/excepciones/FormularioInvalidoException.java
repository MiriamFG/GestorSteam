package excepciones;

import java.util.List;

public class FormularioInvalidoException extends Exception {
    private List<String> errores;

    public FormularioInvalidoException(List<String> errores) {
        super("Errores de validación en el formulario");
        this.errores = errores;
    }

    public List<String> getErrores() {
        return errores;
    }
}
