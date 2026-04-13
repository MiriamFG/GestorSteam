package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.mapper.JuegoMapper;
import org.example.modelo.dto.JuegoDTO;
import org.example.modelo.entidad.JuegoEntidad;
import org.example.modelo.enums.ClasificacionEdad;
import org.example.modelo.enums.EstadoJuego;
import org.example.modelo.form.ErrorDTO;
import org.example.modelo.form.ErrorTipo;
import org.example.modelo.form.JuegoForm;
import org.example.repositorio.interfaces.IJuegoRepo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JuegoControlador {

    private final IJuegoRepo juegoRepo;

    public JuegoControlador(IJuegoRepo juegoRepo) {
        this.juegoRepo = juegoRepo;
    }


    /**
     * Registra un nuevo videojuego en el catálogo del sistema.
     * <p>
     * el método comprueba que el título no esté duplicado
     * para garantizar la integridad del catálogo. Si el título ya existe, se detiene
     * el proceso y se informa de los errores encontrados.
     *
     * @param form Objeto de JuegoForm con los datos del juego (título, precio, género ...).
     * @return un JuegoDTO que representa el juego ya registado con su ID.
     * @throws FormularioInvalidoException Si el título del juego ya existe en el sistema (ErrorTipo.EXISTENTE).
     * @throws IllegalStateException       Si ocurre un error inesperado en el repositorio durante la creación.
     */
    public JuegoDTO aniadirJuego(JuegoForm form) throws FormularioInvalidoException {
        form.validarForumulario();
        List<ErrorDTO> errores = new ArrayList<>();


        if (juegoRepo.obtenerPorTitulo(form.getTitulo()).isPresent()) {
            errores.add(new ErrorDTO("titulo", ErrorTipo.EXISTENTE));
        }

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException((ArrayList<ErrorDTO>) errores);
        }

        JuegoEntidad juego = juegoRepo.crear(form)
                .orElseThrow(() -> new IllegalStateException("No se pudo crear el juego"));

        return JuegoMapper.paraDTO(juego);
    }

    /**
     * Busca juegos en catalago con filtros.
     * <p>
     * Los criterios se aplican con AND, si un parametro es null se ignora el filtro y los resultados por ese campo no se aplican.
     *
     * @param titulo        Fragmento del título a buscar (ignora mayúsculas/minúsculas).
     * @param categoria     Categoría o género exacto del juego.
     * @param precioMin     Límite inferior de precio base.
     * @param precioMax     Límite superior de precio base.
     * @param clasificacion Clasificación por edad (PEGI/ESRB) requerida.
     * @param estado        Estado actual del juego.
     * @return List de JuegoDTO que cumplen con todos los criterios. Si no hay coincidencias devuelve lista vacía.
     */
    public List<JuegoDTO> buscarJuego(String titulo, String categoria, Double precioMin, Double precioMax, ClasificacionEdad clasificacion, EstadoJuego estado) {

        return juegoRepo.obtenerTodos().stream()
                .filter(t -> titulo == null || t.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .filter(c -> categoria == null || c.getCategoria().equalsIgnoreCase(categoria))
                .filter(m -> precioMin == null || m.getPrecioBase() >= precioMin)
                .filter(mx -> precioMax == null || mx.getPrecioBase() <= precioMax)
                .filter(cl -> clasificacion == null || cl.getClasificacionEdad() == clasificacion)
                .filter(e -> estado == null || e.getEstadoJuego() == estado)
                .map(JuegoMapper::paraDTO)
                .toList();
    }

    /**
     * Recupera el catálogo completo de juegos mediante un criterio de ordenacion.
     * <p>
     * Transforma las entididades del catálogo a DTO y aplica el orden antes de devolver la lista.
     * Si el parametro de orden no coincide con los cirterios predefinidos, se devuelve el catálogo en el orden del repositorio.
     *
     * @param orden Criterio de ordenación deseado
     *              <p>
     *              Valores aceptados:
     *              alfabetico: ordena titulo de A a Z,
     *              precio: ordena de menor a mayor,
     *              fecha: ordena por fecha de lanzamiento de mas antiguo a nuevo.
     * @return una List de JuegoDTO ordenada segun el criterio.
     */
    public List<JuegoDTO> consultarCatalogo(String orden) {
        var juegos = juegoRepo.obtenerTodos().stream()
                .map(JuegoMapper::paraDTO)
                .collect(Collectors.toList());


        if ("alfabetico".equalsIgnoreCase(orden)) {
            juegos.stream().sorted(Comparator.comparing(JuegoDTO::getTitulo, String.CASE_INSENSITIVE_ORDER));
        } else if ("precio".equalsIgnoreCase(orden)) {
            juegos.stream().sorted(Comparator.comparingDouble(JuegoDTO::getPrecioBase));
        } else if ("fecha".equalsIgnoreCase(orden)) {
            juegos.stream().sorted(Comparator.comparing(JuegoDTO::getFechaLanz, Comparator.nullsLast(Comparator.naturalOrder())));
        }
        return juegos;

    }

    /**
     * Recupera la información detallada de un juego específico a traves de su ID.
     *
     * @param id Identificador único del juego que se desea consultar.
     * @return un JuegoDTO con la información completa del juego.
     * @throws IllegalArgumentException Si no se encuentra ningún juego con el ID proporcionado.
     */
    public JuegoDTO consultarJuego(Long id) {
        JuegoEntidad juego = juegoRepo.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));

        return JuegoMapper.paraDTO(juego);
    }

    /**
     * Aplica un procentaje de descuento a un juego especifico del catalogo.
     * <p>
     * el descuento es un entero entre 0 y 100, el 0 elimina el descuento previo y el 100 deja el precio final en 0.
     *
     * @param id        Identificador único del juego al que se aplica la rebaja.
     * @param descuento Porcentaje de descuento (0-100).
     * @return un JuegoDTO con la información del juego actualziada.
     * @throws IllegalArgumentException    Si el ID del juego no existe en el sistema.
     * @throws FormularioInvalidoException Si el valor del descuento es nulo, negativo o superior a 100.
     */
    public JuegoDTO aplicarDescuento(Long id, Integer descuento) throws FormularioInvalidoException {

        final int CERO = 0;
        final int CIEN = 100;

        JuegoEntidad juego = juegoRepo.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));

        if (descuento == null || descuento < CERO || descuento > CIEN) {
            List<ErrorDTO> errores = List.of(new ErrorDTO("descuento", ErrorTipo.VALOR_DEMASIADO_ALTO));
            throw new FormularioInvalidoException((ArrayList<ErrorDTO>) errores);
        }
        JuegoForm form = new JuegoForm(
                juego.getTitulo(),
                juego.getDescipcion(),
                juego.getDesarrollador(),
                juego.getFechaLanz(),
                juego.getPrecioBase(),
                descuento,
                juego.getCategoria(),
                juego.getClasificacionEdad(),
                juego.getIdiomasDisponibles(),
                juego.getEstadoJuego()
        );

        JuegoEntidad actualizado = juegoRepo.actualizar(id, form).orElseThrow(() -> new IllegalArgumentException("Error al actualizar"));
        return JuegoMapper.paraDTO(actualizado);
    }

    /**
     * Actualiza el estado de disponiblidad de un juego.
     *
     * @param id          ID del juego.
     * @param nuevoEstado Estado al que se desea cambiar.
     * @return El DTO con el estado ya modificado.
     * @throws IllegalArgumentException Si el estado es nulo o el juego no existe.
     */
    public JuegoDTO cambiarEstado(Long id, EstadoJuego nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("Estado inválido");
        }

        JuegoEntidad juego = juegoRepo.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));

        JuegoForm form = new JuegoForm(
                juego.getTitulo(),
                juego.getDescipcion(),
                juego.getDesarrollador(),
                juego.getFechaLanz(),
                juego.getPrecioBase(),
                juego.getDescuentoActual(),
                juego.getCategoria(),
                juego.getClasificacionEdad(),
                juego.getIdiomasDisponibles(),
                nuevoEstado
        );

        JuegoEntidad actualizado = juegoRepo.actualizar(id, form)
                .orElseThrow(() -> new IllegalArgumentException("Error al persistir el estado"));

        return JuegoMapper.paraDTO(actualizado);
    }

}
