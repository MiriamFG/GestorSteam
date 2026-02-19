package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.entidad.JuegoEntidad;
import org.example.modelo.enums.ClasificacionEdad;
import org.example.modelo.enums.EstadoJuego;
import org.example.modelo.form.ErrorDTO;
import org.example.modelo.form.ErrorTipo;
import org.example.modelo.form.JuegoForm;
import org.example.repositorio.implementacion.JuegoRepoInMemory;
import org.example.repositorio.interfaces.IJuegoRepo;

import java.util.ArrayList;
import java.util.List;

public class JuegoControlador {

    private final IJuegoRepo juegoRepo;

    public JuegoControlador(IJuegoRepo juegoRepo){
        this.juegoRepo = juegoRepo;
    }

    public JuegoDTO añadirJuego(JuegoForm form) throws FormularioInvalidoException{

        List<ErrorDTO> errores = new ArrayList<>();

        if(juegoRepo.obtenerTodos().stream()
                .anyMatch(j -> j.getTitulo().equalsIgnoreCase(form.getTitulo()))){
            errores.add(new ErrorDTO("titulo", ErrorTipo.EXISTENTE));
        }

        if(!errores.isEmpty()) {
            throw new FormularioInvalidoException(errores);
        }

        JuegoEntidad juego = juegoRepo.crear(form)
                .orElseThrow(() -> new IllegalStateException("No se pudo crear el juego"));

        return new JuegoDTO(juego);
    }

    public List<JuegoDTO> buscarJuego(String titulo, String categoria, Double precioMin, Double precioMax, ClasificacionEdad clasificacion, EstadoJuego estado){

        return juegoRepo.obtenerTodos().stream()
                .filter(t -> titulo == null || t.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .filter(c -> categoria == null || c.getCategoria().equalsIgnoreCase(categoria))
                .filter(m ->precioMin == null || m.getPrecioBase() >= precioMin)
                .filter(mx ->precioMax == null || mx.getPrecioBase() <= precioMax)
                .filter(cl -> clasificacion == null || cl.getClasificacionEdad() == clasificacion)
                .filter(e -> estado == null || e.getEstadoJuego() == estado)
                .map(JuegoDTO::new)
                .toList();
    }

    public List<JuegoDTO> consultarCatalogo(String orden){
        var juegos = juegoRepo.obtenerTodos().stream()
                .map(JuegoDTO::new)
                .toList();

        if("alfabetico".equalsIgnoreCase(orden)){
            juegos.stream().sorted((j1, j2)-> j1.getTitulo().compareToIgnoreCase(j2.getTitulo()));
        }else if ("precio".equalsIgnoreCase(orden)){
            juegos.stream().sorted((j1, j2) -> Double.compare(j1.getPrecioBase(), j2.getPrecioBase()));
        }else if("fecha".equalsIgnoreCase(orden)){
            juegos.stream().sorted((j1, j2) -> j1.getFechaLanz().compareTo(j2.getFechaLanz()));
        }
        return juegos;

    }

    public JuegoDTO consultarJuego(Long id){
        JuegoEntidad juego = juegoRepo.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));

        return new JuegoDTO(juego);
    }

    public JuegoDTO aplicarDescuento(Long id, Integer descuento) throws FormularioInvalidoException {
        List<ErrorDTO> errores = new ArrayList<>();

        if(descuento == null || descuento < 0 || descuento > 100){
            errores.add(new ErrorDTO("descuento", ErrorTipo.VALOR_DEMASIADO_ALTO));
        }

        JuegoEntidad juego = juegoRepo.obtenerPorId(id)
                .orElseThrow(()-> new IllegalArgumentException("Juego no econtrado"));

        juego.setDescuentoActual(descuento);
        return new JuegoDTO(juego);

    }

    public JuegoDTO cambiarEstadp(Long id, EstadoJuego nuevoEstado){
        if(nuevoEstado == null){
            throw new IllegalArgumentException("Estado inválido");
        }

        JuegoEntidad juego = juegoRepo.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));

        juego.setEstadoJuego(nuevoEstado);
        return new JuegoDTO(juego);

    }

}
