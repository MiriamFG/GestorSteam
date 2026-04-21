package org.miriam.repositorio.implementacion;

import org.miriam.modelo.entidad.ResenaEntidad;
import org.miriam.modelo.enums.EstadoResena;
import org.miriam.modelo.form.ResenaForm;
import org.miriam.repositorio.interfaces.IResenaRepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResenaRepoInMemory implements IResenaRepo {
    private static List<ResenaEntidad> resenas = new ArrayList<>();
    private static Long idCount = 1L;

    @Override
    public Optional<ResenaEntidad> crear(ResenaForm form) {
        var resena = new ResenaEntidad(idCount++, form.getIdUsuario(), form.getIdJuego(), form.getRecomendado(), form.getTextoResena(), form.getHorasJuegoResena(), LocalDate.now(), LocalDate.now(), EstadoResena.PUBLICADA);
        resenas.add(resena);
        return Optional.of(resena);
    }

    @Override
    public Optional<ResenaEntidad> obtenerPorId(Long id) {
        return resenas.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<ResenaEntidad> obtenerTodos() {
        return new ArrayList<>(resenas);
    }

    @Override
    public Optional<ResenaEntidad> actualizar(Long id, ResenaForm form) {
        var resenaOpc = obtenerPorId(id);
        if (resenaOpc.isEmpty()) {
            throw new IllegalArgumentException("Reseña no encontrada");
        }
        var resenaActualizada = new ResenaEntidad(id, form.getIdUsuario(), form.getIdJuego(),
                form.getRecomendado(), form.getTextoResena(), form.getHorasJuegoResena(), resenaOpc.get().getFechaPubli(), LocalDate.now(),resenaOpc.get().getEstadoResena());
        resenas.removeIf(r -> r.getId().equals(id));
        resenas.add(resenaActualizada);
        return Optional.of(resenaActualizada);
    }

    @Override
    public boolean eliminar(Long id) {
        return resenas.removeIf(r -> r.getId().equals(id));
    }
}
