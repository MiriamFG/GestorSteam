package org.example.repositorio.implementacion;

import org.example.modelo.entidad.BibliotecaEntidad;
import org.example.modelo.form.BibliotecaForm;
import org.example.repositorio.interfaces.IBibliotecaRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BibliotecaRepoInMemory implements IBibliotecaRepo {
    private static List<BibliotecaEntidad> entidad = new ArrayList<>();


    @Override
    public Optional<BibliotecaEntidad> crear(BibliotecaForm form) {
        return Optional.empty();
    }

    @Override
    public Optional<BibliotecaEntidad> obtenerPorId(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<BibliotecaEntidad> obtenerTodos() {
        return List.of();
    }

    @Override
    public Optional<BibliotecaEntidad> actualizar(Long aLong, BibliotecaForm dto) {
        return Optional.empty();
    }

    @Override
    public boolean eliminar(Long aLong) {
        return false;
    }



}
