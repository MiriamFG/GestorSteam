package org.example.modelo.entidad;

import org.example.modelo.enums.EstadoCuenta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class UsuarioEntidad {
    private Long id;
    private String nombreUsuario;
    private String email;
    private String contrasena;
    private String nombreReal;
    private String pais;
    private LocalDate fechaNac;
    private LocalDateTime fechaReg;
    private Optional<String> avatar;
    private double saldoCartera;
    private EstadoCuenta estadoCuenta;

    public UsuarioEntidad(Long id, String nombreUsuario, String email, String contrasena, String nombreReal, String pais, LocalDate fechaNac, LocalDateTime fechaReg, Optional<String> avatar, double saldoCartera, EstadoCuenta estadoCuenta) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.contrasena = contrasena;
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

