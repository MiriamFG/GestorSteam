package org.miriam.modelo.form;

import jakarta.persistence.Column;
import org.miriam.modelo.enums.EstadoCuenta;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UsuarioFormActualizar(
        String nombreUsuario,
        String email,
        String contrasena,
        String nombreReal,
        String pais,
        LocalDate fechaNac,
        LocalDateTime fechaReg,
        String avatar,
        Double saldoCartera,
        EstadoCuenta estadoCuenta

) {

}
