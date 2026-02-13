package org.example.modelo.dto;

import org.example.modelo.enums.EstadoCuenta;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class UsuarioDTO {
    private Long id;
    private String nombreUsuario;
    private String email;
    private String nombreReal;
    private String pais;
    private LocalDate fechaNac;
    private LocalDateTime fechaReg;
    private Optional<String> avatar;
    private Double saldoCartera;
    private EstadoCuenta estadoCuenta;

    public UsuarioDTO(Long id, String nombreUsuario, String email, String nombreReal, String pais, LocalDate fechaNac, LocalDateTime fechaReg, Optional<String> avatar, double saldoCartera, EstadoCuenta estadoCuenta) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.nombreReal = nombreReal;
        this.pais = pais;
        this.fechaNac = fechaNac;
        this.fechaReg = fechaReg;
        this.avatar = avatar;
        this.saldoCartera = saldoCartera;
        this.estadoCuenta = estadoCuenta;
    }

    public Long getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getEmail() {
        return email;
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

    public LocalDateTime getFechaReg() {
        return fechaReg;
    }

    public Optional<String> getAvatar() {
        return avatar;
    }

    public double getSaldoCartera() {
        return saldoCartera;
    }

    public EstadoCuenta getEstadoCuenta() {
        return estadoCuenta;
    }
}
