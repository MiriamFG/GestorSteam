package org.example.repositorio.implementacion;

import org.example.modelo.entidad.CompraEntidad;
import org.example.modelo.form.CompraForm;
import org.example.repositorio.interfaces.ICompraRepo;

import java.util.List;
import java.util.Optional;

public class CompraRepoInMemory implements ICompraRepo {
    @Override
    public Optional<CompraEntidad> crear(CompraForm form) {
        return Optional.empty();
    }

    @Override
    public Optional<CompraEntidad> obtenerPorId(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<CompraEntidad> obtenerTodos() {
        return List.of();
    }

    @Override
    public Optional<CompraEntidad> actualizar(Long aLong, CompraForm dto) {
        return Optional.empty();
    }

    @Override
    public boolean eliminar(Long aLong) {
        return false;
    }
}
