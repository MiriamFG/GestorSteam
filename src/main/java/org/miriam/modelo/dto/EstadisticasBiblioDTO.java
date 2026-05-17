package org.miriam.modelo.dto;

public class EstadisticasBiblioDTO {
    private int totalJuegos;
    private int horasTotales;
    private int juegosInstalados;
    private String juegoMasJugado;
    private double valorTotalBiblioteca;
    private int juegosNuncaJugados;

    public EstadisticasBiblioDTO(int totalJuegos, int horasTotales,
                                 int juegosInstalados, String juegoMasJugado,
                                 double valorTotalBiblioteca, int juegosNuncaJugados) {

        this.totalJuegos = totalJuegos;
        this.horasTotales = horasTotales;
        this.juegosInstalados = juegosInstalados;
        this.juegoMasJugado = juegoMasJugado;
        this.valorTotalBiblioteca = valorTotalBiblioteca;
        this.juegosNuncaJugados = juegosNuncaJugados;
    }

    public int getTotalJuegos() {
        return totalJuegos;
    }

    public int getHorasTotales() {
        return horasTotales;
    }

    public int getJuegosInstalados() {
        return juegosInstalados;
    }

    public String getJuegoMasJugado() {
        return juegoMasJugado;
    }

    public double getValorTotalBiblioteca() {
        return valorTotalBiblioteca;
    }

    public int getJuegosNuncaJugados() {
        return juegosNuncaJugados;
    }

    @Override
    public String toString() {
        return "EstadisticasBiblioDTO{" +
                "totalJuegos=" + totalJuegos +
                ", horasTotales=" + horasTotales +
                ", juegosInstalados=" + juegosInstalados +
                ", juegoMasJugado='" + juegoMasJugado + '\'' +
                ", valorTotalBiblioteca=" + valorTotalBiblioteca +
                ", juegosNuncaJugados=" + juegosNuncaJugados +
                '}';
    }
}
