package org.miriam.modelo.form;

import org.miriam.excepciones.FormularioInvalidoException;

import java.time.LocalDate;
import java.util.ArrayList;

public class UsuarioForm {
    private String nombreUsuario;
    private String email;
    private String contrasena;
    private String nombreReal;
    private String pais;
    private LocalDate fechaNac;
    private String avatar;

    public UsuarioForm(String nombreUsuario, String email, String contrasena, String nombreReal, String pais, LocalDate fechaNac, String avatar) {
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasena = contrasena;
        this.nombreReal = nombreReal;
        this.pais = pais;
        this.fechaNac = fechaNac;
        this.avatar = avatar;

    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getNombreReal() {
        return nombreReal;
    }

    public String getPais() {
        return pais;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public String getAvatar() {
        return avatar;
    }

    /**
     * Valida los datos del formulario de usuario
     * <p>
     * Valida:
     * Que el nombre del usuario no sea nulo o esté vacío
     * Que el nombre del usuario tenga una longitud entre 3 y 20 caraceres
     * Que el nombre del usuario solo tenga alfanumericos, guiones y guiones bajos
     * Que el nombre del usuario no empiece con un número
     * <p>
     * Que el email no sea nulo o esté vacío
     * Que el formato del email sea válido
     * <p>
     * Que la contraseña no sea nulo o esté vacía
     * Que la longitud sea máximo de 8 caracteres
     * Que el formato de la contraseña incluya una mayuscula, una minuscula y un número
     * <p>
     * Que el nombre real del usuario no sea nulo o esté vacío
     * Que la longitud del nombre esté entre los 2 y 50 caracteres
     * <p>
     * Que el país no sea nulo o esté vacío
     * <p>
     * Que la fecha de nacimiento no sea nula
     * Que la edad del usuario sea mayor de 13 años
     * <p>
     * Que el avatar tenga máximo 100 caracteres
     * <p>
     * <p>
     * Si algún campo no cumple las condiciones, se recopilan los errores en una lista
     * y se lanza {@link FormularioInvalidoException}.
     *
     * @throws FormularioInvalidoException
     */

    final int LONGITUD_2 = 2;
    final int LONGITUD_3 = 3;
    final int LONGITUD_20 = 20;
    final int LONGITUD_50 = 50;
    final int LONGITUD_8 = 8;
    final int ANIO_MENOR = 13;
    final int CIEN = 100;

    public void validarFormulario() throws FormularioInvalidoException {
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            errores.add(new ErrorDTO("nombre", ErrorTipo.REQUERIDO));
        } else {
            if (nombreUsuario.length() < LONGITUD_3 || nombreUsuario.length() > LONGITUD_20) {
                errores.add(new ErrorDTO("nombre", ErrorTipo.LONGITUD_INVALIDA, LONGITUD_3, LONGITUD_20));
            }
            if (!nombreUsuario.matches("^[A-Za-z0-9+_.-]+$")) {
                errores.add(new ErrorDTO("nombreUsuario", ErrorTipo.FORMATO_INVALIDO));
            }
            if (Character.isDigit(nombreUsuario.charAt(0))) {
                errores.add(new ErrorDTO("nombreUsuario", ErrorTipo.FORMATO_INVALIDO));
            }
        }

        if (email == null || email.trim().isEmpty()) {
            errores.add(new ErrorDTO("email", ErrorTipo.REQUERIDO));
        } else {
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                errores.add(new ErrorDTO("email", ErrorTipo.FORMATO_INVALIDO));
            }
        }

        if (contrasena == null || contrasena.isEmpty()) {
            errores.add(new ErrorDTO("contrasena", ErrorTipo.REQUERIDO));
        } else {
            if (contrasena.length() < LONGITUD_8) {
                errores.add(new ErrorDTO("contrasena", ErrorTipo.CONTRASENA_VALIDA));
            }

            boolean mayuscula = false;
            boolean minuscula = false;
            boolean numero = false;

            for (int i = 0; i < contrasena.length(); i++) {
                char letra = contrasena.charAt(i);

                if (Character.isUpperCase(letra)) {
                    mayuscula = true;
                }

                if (Character.isLowerCase(letra)) {
                    minuscula = true;
                }

                if (Character.isDigit(letra)) {
                    numero = true;
                }
            }

            if (!mayuscula || !minuscula || !numero) {
                errores.add(new ErrorDTO("contrasena", ErrorTipo.CONTRASENA_VALIDA));
            }
        }

        if (nombreReal == null || nombreReal.trim().isEmpty()) {
            errores.add(new ErrorDTO("nombreReal", ErrorTipo.FORMATO_INVALIDO));
        } else {
            if (nombreReal.length() < LONGITUD_2 || nombreReal.length() > LONGITUD_50) {
                errores.add(new ErrorDTO("nombreReal", ErrorTipo.LONGITUD_INVALIDA, LONGITUD_2, LONGITUD_50));
            }
        }


        if (pais == null || pais.trim().isEmpty()) {
            errores.add(new ErrorDTO("pais", ErrorTipo.REQUERIDO));
        }

        if (fechaNac == null) {
            errores.add(new ErrorDTO("fechaNac", ErrorTipo.REQUERIDO));
        } else {
            int anioAct = LocalDate.now().getYear();
            int anioNac = fechaNac.getYear();

            if (anioAct - anioNac < ANIO_MENOR) {
                errores.add(new ErrorDTO("fechaNac", ErrorTipo.FORMATO_INVALIDO));
            }
        }

        if (avatar != null && avatar.length() > CIEN) {
            errores.add(new ErrorDTO("avatar", ErrorTipo.CAMPO_LARGO, CIEN));
        }

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException(errores);
        }
    }
}


