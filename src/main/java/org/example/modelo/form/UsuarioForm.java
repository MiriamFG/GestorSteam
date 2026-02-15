package org.example.modelo.form;

import org.example.excepciones.FormularioInvalidoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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


    public void validarForumulario() throws FormularioInvalidoException {
        List<ErrorDTO> errores = new ArrayList<>();

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            errores.add(new ErrorDTO("nombre", ErrorTipo.REQUERIDO));
        }else{
            if(nombreUsuario.length()<3 || nombreUsuario.length() > 20){
                errores.add(new ErrorDTO("nombre", ErrorTipo.LONGITUD_INVALIDA, 3, 20));
            }
            if(nombreUsuario.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
                errores.add(new ErrorDTO("nombreUsuario", ErrorTipo.FORMATO_INVALIDO));
            }
        }

        if(email == null || email.trim().isEmpty()){
            errores.add(new ErrorDTO("email", ErrorTipo.REQUERIDO));
        }else {
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                errores.add(new ErrorDTO("email", ErrorTipo.FORMATO_INVALIDO));
            }
        }

        if(contrasena == null || contrasena.isEmpty()){
            errores.add(new ErrorDTO("contrasena", ErrorTipo.REQUERIDO));
        }else{
            if(contrasena.length() < 8){
                errores.add(new ErrorDTO("contrasena", ErrorTipo.CONTRASENA_VALIDA));
            }

            if (!contrasena.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                errores.add(new ErrorDTO("contraseña", ErrorTipo.FORMATO_INVALIDO));
            }

        }

        if(nombreReal == null || nombreReal.trim().isEmpty()){
            errores.add(new ErrorDTO("email", ErrorTipo.FORMATO_INVALIDO));
        }else{
            if(nombreReal.length()>2 || nombreReal.length() > 50){
                errores.add(new ErrorDTO("nombreReal", ErrorTipo.LONGITUD_INVALIDA, 2, 50));
            }
        }


        if(pais == null || pais.trim().isEmpty()){
            errores.add(new ErrorDTO("pais", ErrorTipo.REQUERIDO));
        }

        if(fechaNac == null){
            errores.add(new ErrorDTO("fechaNac", ErrorTipo.REQUERIDO));
        }else{
            int anioAct = LocalDate.now().getYear();
            int anioNac = fechaNac.getYear();

            if(fechaNac == null || anioAct - anioNac < 13);
            errores.add(new ErrorDTO("fechaNac", ErrorTipo.FORMATO_INVALIDO));
        }

        if(avatar != null && avatar.length() > 100){
            errores.add(new ErrorDTO("avatar", ErrorTipo.CAMPO_LARGO, 100));
        }else {
            throw new FormularioInvalidoException(errores);
        }
    }
}


