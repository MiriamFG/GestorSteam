package org.example.repositorio.implementacion;

import org.example.modelo.entidad.BibliotecaEntidad;
import org.example.modelo.enums.EstadoInstalacion;
import org.example.modelo.form.BibliotecaForm;
import org.example.repositorio.interfaces.IBibliotecaRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BibliotecaRepoInMemory implements IBibliotecaRepo {
    private static List<BibliotecaEntidad> bibliotecas = new ArrayList<>();
    private static Long idCount = 1L;


    @Override
    public Optional<BibliotecaEntidad> crear(BibliotecaForm form) {
        var biblioteca = new BibliotecaEntidad(idCount++, form.getIdUsuario(), form.getIdJuego(), form.getFechaAdquisicion(), form.getNumHorasTotal(), form.getUltimaFechaJuego(), form.getEstadoInstalacion());
        bibliotecas.add(biblioteca);
        return Optional.of(biblioteca);
    }

    @Override
    public Optional<BibliotecaEntidad> obtenerPorId(Long id) {
        return bibliotecas.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<BibliotecaEntidad> obtenerTodos() {
        return new ArrayList<>(bibliotecas);
    }

    @Override
    public Optional<BibliotecaEntidad> actualizar(Long id, BibliotecaForm form) {
        var biblioOpc = obtenerPorId(id);
        if(biblioOpc.isEmpty()){
            throw new IllegalArgumentException("Biblioteca no encontrada");
        }
        var bibliotecaActualizada = new BibliotecaEntidad(id, form.getIdUsuario(), form.getIdJuego(), form.getFechaAdquisicion(), form.getNumHorasTotal(), form.getUltimaFechaJuego(), form.getEstadoInstalacion());
        bibliotecas.removeIf(b -> b.getId().equals(id));
        bibliotecas.add(bibliotecaActualizada);
        return Optional.of(bibliotecaActualizada);
    }

    @Override
    public boolean eliminar(Long id) {
        return bibliotecas.removeIf(b -> b.getId().equals(id));
    }

}
