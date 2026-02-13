package org.example.modelo.form;

public enum ErrorTipo {

    REQUERIDO("El campo es obligatorio"),
    FORMATO_INVALIDO("El formato es inválido"),
    VALOR_DEMASIADO_ALTO("El valor es demasiado alto"),
    VALOR_DEMASIADO_BAJO("El valor es demasiado bajo"),
    NO_ENCONTRADO("No se encontró el elemento"),
    FECHA_FUTURA("futura no válida"),
    FECHA_OBLIGATORIA("es obligatoria"),
    DUPLICADO("El elemento está duplicado");


    private final String mensaje;

    private ErrorTipo(String mensaje) {
        this.mensaje = mensaje;
    }
}
