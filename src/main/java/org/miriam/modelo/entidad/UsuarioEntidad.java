package org.miriam.modelo.entidad;

import jakarta.persistence.*;
import org.miriam.modelo.enums.EstadoCuenta;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Table(name = "usuarios")
@Entity

public class UsuarioEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre_usuario")
    private String nombreUsuario;
    @Column(name = "email")
    private String email;
    @Column(name = "contrasena")
    private String contrasena;
    @Column(name = "nombre_real")
    private String nombreReal;
    @Column(name = "pais")
    private String pais;
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNac;
    @Column(name = "fecha_registro")
    private LocalDateTime fechaReg;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "saldo_cartera")
    private Double saldoCartera;
    @Column(name = "estado_cuenta")
    private EstadoCuenta estadoCuenta;

    public UsuarioEntidad() {

    }

    public UsuarioEntidad(Long id, String nombreUsuario, String email, String contrasena, String nombreReal, String pais, LocalDate fechaNac, LocalDateTime fechaReg, String avatar, Double saldoCartera, EstadoCuenta estadoCuenta) {
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

    public String getAvatar() {
        return avatar;
    }

    public Double getSaldoCartera() {
        return saldoCartera;
    }

    public EstadoCuenta getEstadoCuenta() {
        return estadoCuenta;
    }

}


