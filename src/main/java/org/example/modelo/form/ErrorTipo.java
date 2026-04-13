package org.example.modelo.form;

public enum ErrorTipo {

    REQUERIDO("El campo es obligatorio"),
    FORMATO_INVALIDO("El formato es inválido"),
    VALOR_DEMASIADO_ALTO("El valor es demasiado alto"),
    VALOR_DEMASIADO_BAJO("El valor es demasiado bajo"),
    NO_ENCONTRADO("No se encontró el elemento"),
    LONGITUD_INVALIDA("el campo debe tener entre %d y &d caracteres"),
    CONTRASENA_VALIDA("La contraseña debe ter al menos 8 caracteres"),
    CAMPO_LARGO("El campo no puede superar los % caracteres"),
    MAX_DECIMALES("Maximo % decimales permitidos"),
    SALDO_INSUFICIENTE("el saldo es insuficiente"),
    CAMPO_ENTRE("El campo debe estar entre %d y %d"),
    PRECIO_DECIMALES("El precio base no puede tener más de 2 decimales"),
    FECHA_FUTURA("futura no válida"),
    FECHA_OBLIGATORIA("es obligatoria"),
    FECHA_INVALIDA("es invalida"),
    EXISTENTE("ya existe en el sistema"),
    REGISTRADO("ya está registrado en el sistema"),
    NO_ACTIVO("el elemento está activo"),
    NO_PROPIETARIO("no tienes este juego"),
    DUPLICADO("El elemento está duplicado"),
    PROHIBIDO("el usuario no tiene acceso");


    private final String mensaje;

    private ErrorTipo(String mensaje) {
        this.mensaje = mensaje;
    }
}
