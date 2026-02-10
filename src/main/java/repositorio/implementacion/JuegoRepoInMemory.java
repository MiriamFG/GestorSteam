package repositorio.implementacion;

import modelo.entidad.JuegoEntidad;
import modelo.form.JuegoForm;
import repositorio.interfaces.IJuegoRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JuegoRepoInMemory implements IJuegoRepo {
    private static final List<JuegoEntidad> juegos = new ArrayList<>();
    private static Long idCount = 1L;
    @Override
    public Optional<JuegoEntidad> crear(JuegoForm form) {
        var juego = new JuegoEntidad(idCount, form.getTitulo(), form.getDescipcion(), form.getDesarrollador(), form.getFechaLanz(), form.getPrecioBase(), form.getDescuentoActual(), form.getCategoria(), form.getClasificacionEdad(), form.getIdiomasDisponibles(), form.getEstadoJuego());
        juegos.add(juego);
        return Optional.of(juego);
    }

    @Override
    public Optional<JuegoEntidad> obtenerPorId(Long id) {
        return juegos.stream()
                .filter(j -> j.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<JuegoEntidad> obtenerTodos() {
        return new ArrayList<>(juegos);
    }

    @Override
    public Optional<JuegoEntidad> actualizar(Long id, JuegoForm form) {
        var juegoOpc = obtenerPorId(id);
        if(juegoOpc.isEmpty()){
            throw new IllegalArgumentException("Juego no encontrado");
        }
        var juegoActualizado = new JuegoEntidad(form.getId(),form.getTitulo(), form.getDescipcion(), form.getDesarrollador(), form.getFechaLanz(), form.getPrecioBase(), form.getDescuentoActual(), form.getCategoria(), form.getClasificacionEdad(), form.getIdiomasDisponibles(), form.getEstadoJuego());
        juegos.removeIf(j -> j.getId().equals(id));
        juegos.add(juegoActualizado);
        return Optional.of(juegoActualizado);
    }

    @Override
    public boolean eliminar(Long id) {
        return juegos.removeIf(j -> j.getId().equals(id));
    }

    @Override
    public Optional<JuegoEntidad> obtenerPorTitulo(String titulo){
        return juegos.stream()
                .filter(j -> j.getTitulo().equalsIgnoreCase(titulo))
                .findFirst();
    }
}
