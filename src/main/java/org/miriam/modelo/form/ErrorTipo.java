package org.miriam.modelo.form;

public enum ErrorTipo {

    // --- ERRORES GENERALES DE VALIDACIÓN ---
    REQUERIDO("El campo es obligatorio."),
    FORMATO_INVALIDO("El formato es inválido."),
    VALOR_INVALIDO("El valor proporcionado es inválido."),
    VALOR_DEMASIADO_ALTO("El valor es demasiado alto."),
    VALOR_DEMASIADO_BAJO("El valor es demasiado bajo."),
    LONGITUD_INVALIDA("El campo debe tener entre %d y %d caracteres."),
    CAMPO_LARGO("El campo no puede superar los %d caracteres."),
    CAMPO_ENTRE("El campo debe estar entre %d y %d."),
    MAX_DECIMALES("Máximo %d decimales permitidos."),

    // --- ERRORES DE NEGOCIO / ENTIDADES ---
    NO_ENCONTRADO("No se encontró el elemento solicitado."),
    EXISTENTE("El elemento ya existe en el sistema."),
    DUPLICADO("El elemento está duplicado."),
    NO_ACTIVO("El elemento no está activo."),
    ESTADO_INVALIDO("El estado no es válido."),
    SALDO_INSUFICIENTE("El saldo es insuficiente."),
    REGISTRADO("El usuario ya está registrado en el sistema."),

    // --- ERRORES DE SEGURIDAD / ACCESO ---
    PROHIBIDO("El usuario no tiene acceso a este recurso."),
    NO_PROPIETARIO("No eres el propietario de este juego."),
    CONTRASENA_DEBIL("La contraseña debe tener al menos 8 caracteres."),
    CONTRASENA_CORTA("La contraseña debe tener al menos %d caracteres."),

    // --- ERRORES DE FECHAS ---
    FECHA_OBLIGATORIA("La fecha es obligatoria."),
    FECHA_INVALIDA("La fecha introducida es inválida."),
    FECHA_FUTURA("La fecha no puede ser futura."),

    // --- ERRORES DE OPERACIONES / BD ---
    ERROR_CREACION("Error al crear el elemento."),
    NO_ACTUALIZADO("No se ha podido actualizar el elemento."), ;


    private final String mensaje;

    private ErrorTipo(String mensaje) {
        this.mensaje = mensaje;
    }
    /**
     * Permite obtener el mensaje formateado dinámicamente si contiene %d o %s.
     * Ejemplo: ErrorTipo.LONGITUD_INVALIDA.getMensajeFormateado(3, 10);
     */
    public String getMensajeFormateado(Object... args) {
        if (args == null || args.length == 0) {
            return this.mensaje;
        }
        return String.format(this.mensaje, args);
    }
}
