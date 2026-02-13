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
        List<String> errores = new ArrayList<>();
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            errores.add("El nombre es obligatorio");
        }else{
            if(nombreUsuario.length()<3 || nombreUsuario.length() > 20){
                errores.add("El nombre debe tener entre 3 y 20 caracteres");
            }
            if(nombreUsuario.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
                errores.add("El nombre solo puede contener letras, numeros, guiones y no puede empezar por número");
            }
        }

        if(email == null || email.trim().isEmpty()){
            errores.add("el email es obligatorio");
        }else {
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                errores.add("El email no tiene un formato válido");
            }
        }

        if(contrasena == null || contrasena.isEmpty()){
            errores.add("La contraseña es obligatoria");
        }else{
            if(contrasena.length() < 8){
                errores.add("La contraseña debe ter al menos 8 caracteres");
            }

            if (!contrasena.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                errores.add("La contraseña no tiene un formato válido");
            }

        }

        if(nombreReal == null || nombreReal.trim().isEmpty()){
            errores.add("El nombre es obligatorio");
        }else{
            if(nombreReal.length()>2 || nombreReal.length() > 50){
                errores.add("El nombre debe tener entre 2 y 50 caracteres");
            }
        }


        if(pais == null || pais.trim().isEmpty()){
            errores.add("el país es obligatorio");
        }

        if(fechaNac == null){
            errores.add("la fecha de nacimiento es obligatoria");
        }else{
            int anioAct = LocalDate.now().getYear();
            int anioNac = fechaNac.getYear();

            if(fechaNac == null || anioAct - anioNac < 13);
            errores.add("Año vacio o menor de 13");
        }

        if(avatar != null && avatar.length() > 100){
            errores.add("El avatar no puede superar los 100 caracteres");
        }else {
            throw new FormularioInvalidoException(errores);
        }
    }
}


