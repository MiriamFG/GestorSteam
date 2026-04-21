package org.miriam.repositorio.implementacion;

import java.util.ArrayList;
import java.util.List;

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
