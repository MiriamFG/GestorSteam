package org.example.modelo.dto;

import java.time.LocalDate;

public class SesionInfoDTO {
    private LocalDate fecha;
    private Long diasTranscurridos;
    private boolean nuncaJugado;

    public SesionInfoDTO(LocalDate fecha, Long diasTranscurridos, boolean nuncaJugado) {
        this.fecha = fecha;
        this.diasTranscurridos = diasTranscurridos;
        this.nuncaJugado = nuncaJugado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Long getDiasTranscurridos() {
        return diasTranscurridos;
    }

    public boolean isNuncaJugado() {
        return nuncaJugado;
    }
}
