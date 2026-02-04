package modelo.form;

import excepciones.FormularioInvalidoException;
import modelo.enums.EstadoCuenta;

import java.sql.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioForm {
    public Long id;
    public String nombreUsuario;
    public String email;
    public String contrasena;
    public String nombreReal;
    public String pais;
    public LocalDate fechaNac;
    public LocalDateTime fechaReg;
    public String avatar;
    public double saldoCartera;
    public EstadoCuenta estadoCuenta;

    public void validarForumulario(String nombreReal, String email, LocalDate fechaNac) throws FormularioInvalidoException {
        List<String> errores = new ArrayList<>();
        if (nombreReal == null || nombreReal.trim().isEmpty()) {
            errores.add("El nombre es obligatorio");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errores.add("El email no es válido");
        }
        LocalDate fechaMin = LocalDate.now().minusYears(13);
        if(fechaNac == null || fechaNac.isAfter(LocalDate.now()) || fechaNac.isAfter(fechaMin))

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException(errores);
        }
    }
}


