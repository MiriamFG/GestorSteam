package org.miriam.controlador;

import org.miriam.excepciones.FormularioInvalidoException;
import org.miriam.mapper.JuegoMapper;
import org.miriam.mapper.ResenaMapper;
import org.miriam.mapper.UsuarioMapper;
import org.miriam.modelo.dto.JuegoDTO;
import org.miriam.modelo.dto.ResenaDTO;
import org.miriam.modelo.dto.UsuarioDTO;
import org.miriam.modelo.entidad.BibliotecaEntidad;
import org.miriam.modelo.entidad.ResenaEntidad;
import org.miriam.modelo.enums.EstadoResena;
import org.miriam.modelo.form.ErrorDTO;
import org.miriam.modelo.form.ErrorTipo;
import org.miriam.modelo.form.ResenaForm;
import org.miriam.repositorio.interfaces.*;

import java.util.ArrayList;
import java.util.List;

public class ResenaControlador {
    private final IResenaRepo resenaRepo;
    private final ICompraRepo compraRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;
    private final IBibliotecaRepo bibliotecaRepo;
    private final BibliotecaControlador bibliotecaControlador;

    public ResenaControlador(IResenaRepo resenaRepo, ICompraRepo compraRepo, IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo, IBibliotecaRepo bibliotecaRepo, BibliotecaControlador bibliotecaControlador) {
        this.resenaRepo = resenaRepo;
        this.compraRepo = compraRepo;
        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
        this.bibliotecaRepo = bibliotecaRepo;
        this.bibliotecaControlador = bibliotecaControlador;
    }

    /**
     * Crea y registra una nueva reseña para un juego en el sistema.
     * <p>
     * Realiza comprobaciones de reglas de negocio antes de ejecutar,
     * verifica en la biblioteca que el usuario tenga el juego,
     * extrae las horas de juego actuales del registro de la Biblioteca,
     * valida que el usuario no haya escrito ya una reseña para el juego.
     *
     * @param idUsuario   Identificador del usuario que desea escribir la reseña.
     * @param idJuego     Identificador del juego que se va a valorar.
     * @param recomendado Valor booleano que indica si el usuario recomienda (true) o no (false) el juego.
     * @param texto       contenido de texto de la reseña.
     * @return un ResenaDTO que representa la reseña creada con exito.
     * @throws FormularioInvalidoException Si el usuario no es propietario del juego (ErrorTipo.NO_PROPIETARIO),
     *                                     o si ya existe una reseña previa (ErrorTipo.DUPLICADO).
     * @throws RuntimeException            Si ocurre un error inesperado al crear la reseña en el repositorio.
     */
    public ResenaDTO escribirResena(Long idUsuario, Long idJuego, Boolean recomendado, String texto) throws FormularioInvalidoException {
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        BibliotecaEntidad registroBiblio = null;
        for (BibliotecaEntidad b : bibliotecaRepo.obtenerTodos()) {
            if (b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)) {
                registroBiblio = b;
                break;
            }
        }

        if (registroBiblio == null) {
            errores.add(new ErrorDTO("juego", ErrorTipo.NO_PROPIETARIO));
        }

        for (ResenaEntidad r : resenaRepo.obtenerTodos()) {
            if (r.getUsuarioId().equals(idUsuario) && r.getJuegoId().equals(idJuego)) {
                errores.add(new ErrorDTO("resena", ErrorTipo.DUPLICADO));
                break;
            }
        }

        if (!errores.isEmpty()) throw new FormularioInvalidoException(errores);

        ResenaForm form = new ResenaForm(
                idUsuario,
                idJuego,
                recomendado,
                texto,
                registroBiblio.getNumHorasTotal().doubleValue()
        );

        ResenaEntidad nueva = resenaRepo.crear(form)
                .orElseThrow(() -> new IllegalArgumentException("Error al crear reseña"));

        UsuarioDTO userDTO = usuarioRepo.obtenerPorId(idUsuario)
                .map(UsuarioMapper::paraDTO).orElse(null);
        JuegoDTO juegoDTO = juegoRepo.obtenerPorId(idJuego)
                .map(JuegoMapper::paraDTO).orElse(null);

        return ResenaMapper.paraDTO(nueva, userDTO, juegoDTO);
    }

    /**
     * Consigue la lista de reseñas publicas de un juego especifico  permitiendo filtrar por valoracion.
     * <p>
     * Niveles filtrado:
     * pertenencia al juego indicado,
     * visibilidad publica,
     * filtro de recomendacion.
     *
     * @param idJuego Identificador del juego cuyas reseñas se desean consultar.
     * @param filtro  Criterio de filtrado opcional.
     *                Valores aceptados: "positiva" (solo recomendados),"negativa" (solo no recomendados).
     *                Cualquier otro valor o null devolverá todas las reseñas.
     * @return una List de ResenaDTO con las reseñas que cumplen los criterios,
     * Devuelve lista vacia si no hay coincidencias.
     */
    public List<ResenaDTO> verResenasJuego(Long idJuego, String filtro) {
        List<ResenaDTO> resultado = new ArrayList<>();

        JuegoDTO juegoDTO = juegoRepo.obtenerPorId(idJuego)
                .map(JuegoMapper::paraDTO)
                .orElse(null);

        for (ResenaEntidad r : resenaRepo.obtenerTodos()) {

            if (r.getJuegoId().equals(idJuego) && r.getEstadoResena() == EstadoResena.PUBLICADA) {

                if (filtro != null) {
                    if (filtro.equalsIgnoreCase("positiva") && !r.getRecomendado()){
                        continue;
                    }
                    if (filtro.equalsIgnoreCase("negativa") && r.getRecomendado()){
                        continue;
                    }
                }

                UsuarioDTO usuarioDTO = usuarioRepo.obtenerPorId(r.getUsuarioId())
                        .map(UsuarioMapper::paraDTO)
                        .orElse(null);
                resultado.add(ResenaMapper.paraDTO(r, usuarioDTO, juegoDTO));
            }
        }
        return resultado;
    }

    /**
     * Recupera el historial de reseñas publicadas o ocultas de un usuario.
     * <p>
     * El metodo recorre el repositorio de reseñas filtrando por el ID del autor, incluyendo las reseñas con estado OCULTA.
     * <p>
     * Filtrado:
     * se incliyen reseñas en estado PUBLICADA y OCULTA,
     * se exluyen las ELIMINADAS.
     *
     * @param idUsuario Identificador único del usuario cuyas reseñas se desean consultar.
     * @return Una List de ResenaDTO con las reseñas del usuario.
     * Si el usuario no tiene reseñas o han sido todas eliminadas devuelve una lista vacía.
     */
    public List<ResenaDTO> verResenasUsuario(Long idUsuario) {
        List<ResenaDTO> resultado = new ArrayList<>();

        UsuarioDTO usuarioDTO = usuarioRepo.obtenerPorId(idUsuario)
                .map(UsuarioMapper::paraDTO)
                .orElse(null);

        for (ResenaEntidad r : resenaRepo.obtenerTodos()) {

            if (r.getUsuarioId().equals(idUsuario)) {

                if (r.getEstadoResena() != EstadoResena.ELIMINADA) {
                    JuegoDTO juegoDTO = juegoRepo.obtenerPorId(r.getJuegoId())
                            .map(JuegoMapper::paraDTO)
                            .orElse(null);
                    resultado.add(ResenaMapper.paraDTO(r, usuarioDTO, juegoDTO));
                }
            }
        }
        return resultado;
    }

    /**
     * Cambia el estado de una reseña a OCULTA.
     * <p>
     * cuando está OCULTA la reseña deja de ser visible para los usuarios pero permanece en el historial del usuario dueño.
     * <p>
     * Validaciones:
     * la reseña debe existir en el repositorio,
     * solo el dueño de la reserña puede ocultarla.
     *
     * @param idResena  es el identificador unico de la reseña a ocultar.
     * @param idUsuario identificador del usuario que quiere ocultar la reseña.
     * @throws IllegalArgumentException si no encuentra la reeña con el ID indicado.
     * @throws RuntimeException         si el usuario intenta ocultar una reseña que no es suya.
     */
    public void ocultarResena(Long idResena, Long idUsuario) {
        ResenaEntidad resena = resenaRepo.obtenerPorId(idResena)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        if (!resena.getUsuarioId().equals(idUsuario)) {
            throw new IllegalArgumentException("las reseñas que no son tuyas no pueden ocultarse");
        }

        ResenaForm formOcultar = new ResenaForm(
                resena.getUsuarioId(),
                resena.getJuegoId(),
                resena.getRecomendado(),
                resena.getTextoResena(),
                resena.getHorasJuegoResena(),
                EstadoResena.OCULTA
        );

        resenaRepo.actualizar(idResena, formOcultar);
    }

    /**
     * Borra una reseña existente.
     * <p>
     * No elimina el registro, solo cambia el estado mediante la actualizacion.
     * <p>
     * Validaciones realizadas:
     * verifica que la reseña con el ID de reseña exista,
     * comprueba que el ID del usuario coincida con el autor de la reseña.
     *
     * @param idResena  es el identificador unico de la reseña a eliminar.
     * @param idUsuario identificador del usuario que quiere eliminar la reseña.
     * @throws IllegalArgumentException si no encuentra la reeña con el ID indicado.
     * @throws RuntimeException         si el usuario intenta eliminar una reseña que no es suya.
     */
    public void eliminarResena(Long idResena, Long idUsuario) {
        ResenaEntidad resena = resenaRepo.obtenerPorId(idResena)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));

        if (!resena.getUsuarioId().equals(idUsuario)) {
            throw new IllegalArgumentException("las reseñas que no son tuyas no pueden eliminarse");
        }
        ResenaForm formEliminar = new ResenaForm(
                resena.getUsuarioId(),
                resena.getJuegoId(),
                resena.getRecomendado(),
                resena.getTextoResena(),
                resena.getHorasJuegoResena(),
                EstadoResena.ELIMINADA
        );

        resenaRepo.actualizar(idResena, formEliminar);
    }


}
