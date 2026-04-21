package org.miriam.modelo.dto;

public class EstadisticasBiblioDTO {
    private int totalJuegos;
    private int horasTotales;
    private int juegosInstalados;
    private String juegoMasJugado;
    private double valorTotalBiblioteca;
    private int juegosNuncaJugados;

    public EstadisticasBiblioDTO(int totalJuegos, int horasTotales, int juegosInstalados, String juegoMasJugado, double valorTotalBiblioteca, int juegosNuncaJugados) {
        this.totalJuegos = totalJuegos;
        this.horasTotales = horasTotales;
        this.juegosInstalados = juegosInstalados;
        this.juegoMasJugado = juegoMasJugado;
        this.valorTotalBiblioteca = valorTotalBiblioteca;
        this.juegosNuncaJugados = juegosNuncaJugados;
    }
}
