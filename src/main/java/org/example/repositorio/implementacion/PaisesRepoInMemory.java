package org.example.repositorio.implementacion;

import org.example.modelo.entidad.JuegoEntidad;
import org.example.modelo.form.JuegoForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaisesRepoInMemory {

    private List<String> paisesValidos = List.of(
            "España", "Francia", "Alemania", "Italia", "Portugal"
    );

    public List<String> obtenerTodos() {
        return new ArrayList<>(paisesValidos);
    }

    public void actualizar(String pais) {
        paisesValidos.add(pais);
    }
}
