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
import org.miriam.modelo.form.ErrorDto;
import org.miriam.modelo.form.ErrorTipo;
import org.miriam.modelo.form.ResenaForm;
import org.miriam.repositorio.interfaces.*;
import org.miriam.transaction.ITransactionManager;

import java.util.ArrayList;
import java.util.List;

public class ResenaControlador {
    private final IResenaRepo resenaRepo;
    private final ICompraRepo compraRepo;
    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;
    private final IBibliotecaRepo bibliotecaRepo;

    public ITransactionManager tm;


    public ResenaControlador(IResenaRepo resenaRepo, ICompraRepo compraRepo, IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo, IBibliotecaRepo bibliotecaRepo, ITransactionManager tm) {
        this.resenaRepo = resenaRepo;
        this.compraRepo = compraRepo;
        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
        this.bibliotecaRepo = bibliotecaRepo;
        this.tm = tm;

    }

    /**
     * Gestiona la creación y persistencia de una nueva reseña en la plataforma.
     * <p>
     * El método realiza un flujo completo de validaciones de negocio antes de registrar la reseña:
     * </p>
     * <ul>
     *     <li>Valida el formato e integridad inicial del formulario recibido.</li>
     *     <li>Verifica la existencia real tanto del usuario como del juego en el sistema.</li>
     *     <li>Comprueba que el usuario sea propietario del juego (que exista en su biblioteca).</li>
     *     <li><b>Validación extra:</b> Deniega la operación si las horas de juego declaradas en el
     *     formulario superan a las horas totales registradas realmente en la biblioteca del usuario.</li>
     *     <li>Garantiza que no existan reseñas previas del mismo usuario para el mismo juego (evita duplicados).</li>
     * </ul>
     * <p>
     * Una vez superados los filtros, se fuerza el registro de la reseña con las horas reales de la
     * biblioteca y se establece su estado como {@code PUBLICADA} de forma transaccional.
     * </p>
     *
     * @param form Objeto {@link ResenaForm} que contiene los datos de la reseña introducidos por el usuario.
     * @return Un {@link ResenaDTO} con toda la información de la reseña creada, incluyendo los datos mapeados del usuario y del juego.
     * @throws FormularioInvalidoException Si el formato del formulario es incorrecto, si el usuario o juego no existen,
     *                                     si el usuario no es propietario del juego, si las horas declaradas son inválidas,
     *                                     o si el usuario ya ha reseñado este juego previamente.
     */
    public ResenaDTO crearResena(ResenaForm form) throws FormularioInvalidoException {
        form.validarFormulario();
        return tm.inTransaction(() -> {
            ArrayList<ErrorDto> errores = new ArrayList<>();

            boolean existeUsuario = usuarioRepo.obtenerPorId(form.getIdUsuario()).isPresent();
            if (!existeUsuario) {
                errores.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
            }

            boolean existeJuego = juegoRepo.obtenerPorId(form.getIdJuego()).isPresent();
            if (!existeJuego) {
                errores.add(new ErrorDto("juego", ErrorTipo.NO_ENCONTRADO));
            }
            BibliotecaEntidad registroBiblio = null;
            if (existeUsuario && existeJuego) {
                for (BibliotecaEntidad b : bibliotecaRepo.obtenerTodos()) {
                    if (b.getUsuarioId().equals(form.getIdUsuario()) && b.getJuegoId().equals(form.getIdJuego())) {
                        registroBiblio = b;
                        break;
                    }
                }
                if (registroBiblio == null) {
                    errores.add(new ErrorDto("juego", ErrorTipo.NO_PROPIETARIO));
                } else {
                    if (form.getHorasJuegoResena() != null && form.getHorasJuegoResena() > registroBiblio.getNumHorasTotal().doubleValue()) {
                        errores.add(new ErrorDto("horasJuego", ErrorTipo.VALOR_INVALIDO));
                    }
                }
            }

            for (ResenaEntidad r : resenaRepo.obtenerTodos()) {
                if (r.getUsuarioId().equals(form.getIdUsuario()) && r.getJuegoId().equals(form.getIdJuego())) {
                    errores.add(new ErrorDto("resena", ErrorTipo.DUPLICADO));
                    break;
                }
            }

            if (!errores.isEmpty()) {
                throw new FormularioInvalidoException(errores);
            }

            double horas = registroBiblio.getNumHorasTotal().doubleValue();


            ResenaForm formParaPersistir = new ResenaForm(form.getIdUsuario(), form.getIdJuego(), form.getRecomendado(), form.getTextoResena(), horas, EstadoResena.PUBLICADA);

            ResenaEntidad nueva = resenaRepo.crear(formParaPersistir).orElseThrow(() -> new IllegalArgumentException("Error al crear reseña"));

            UsuarioDTO userDTO = usuarioRepo.obtenerPorId(form.getIdUsuario()).map(UsuarioMapper::paraDTO).orElse(null);
            JuegoDTO juegoDTO = juegoRepo.obtenerPorId(form.getIdJuego()).map(JuegoMapper::paraDTO).orElse(null);

            return ResenaMapper.paraDTO(nueva, userDTO, juegoDTO);
        });
    }

    /**
     * Crea y publica una reseña simplificada a partir de parámetros individuales.
     * <p>
     * Este método actúa como un método de conveniencia (helper) que empaqueta los datos
     * básicos de la reseña —identificadores, veredicto de recomendación y el cuerpo del texto—
     * en un objeto {@link ResenaForm}. Por defecto, inicializa el contador de horas en {@code 0.0}
     * y establece el estado inicial como {@code PUBLICADA}.
     * </p>
     * <p>
     * Una vez construido el formulario, delega la ejecución, el procesamiento transaccional
     * y todo el flujo de validaciones de negocio en el método principal {@link #crearResena(ResenaForm)}.
     * </p>
     *
     * @param idUsuario   Identificador único del usuario que escribe la reseña.
     * @param idJuego     Identificador único del juego que está siendo evaluado.
     * @param recomendado Valor booleano que indica si el usuario recomienda ({@code true}) o no ({@code false}) el juego.
     * @param textoResena Contenido textual con la opinión o comentarios del usuario.
     * @return Un {@link ResenaDTO} que contiene la información de la reseña completamente registrada y mapeada.
     * @throws FormularioInvalidoException Si la información combinada viola cualquiera de las reglas de negocio
     *                                     verificadas en el flujo principal de creación de reseñas.
     */
    public ResenaDTO escribirResena(Long idUsuario, Long idJuego, Boolean recomendado, String textoResena) throws FormularioInvalidoException {
        ResenaForm form = new ResenaForm(idUsuario, idJuego, recomendado, textoResena, 0.0, org.miriam.modelo.enums.EstadoResena.PUBLICADA);
        return crearResena(form);
    }

    /**
     * Recupera y lista todas las reseñas públicas asociadas a un juego específico.
     * <p>
     * El método inicia una transacción para comprobar en primer lugar la existencia del juego
     * en el sistema. Si el juego existe, mapea sus datos a un DTO y recorre el histórico de reseñas
     * de la plataforma para filtrar únicamente aquellas que pertenezcan al identificador del juego
     * solicitado y cuyo estado sea {@code PUBLICADA}.
     * </p>
     * <p>
     * Durante el volcado de datos, el método asocia de forma dinámica la información de perfil
     * del autor correspondiente a cada reseña para construir la colección final de resultados.
     * </p>
     *
     * @param idJuego Identificador único del juego cuyas reseñas se desean consultar.
     * @return Una {@link List} de {@link ResenaDTO} que contiene todas las reseñas públicas del juego.
     * Si el juego no tiene reseñas, devolverá una lista vacía.
     * @throws FormularioInvalidoException Si el identificador proporcionado no corresponde a ningún
     *                                     juego registrado en la base de datos.
     */
    public List<ResenaDTO> listarResenasJuego(Long idJuego) throws FormularioInvalidoException {
        return tm.inTransaction(() -> {
            List<ResenaDTO> resultado = new ArrayList<>();

            boolean existeJuego = juegoRepo.obtenerPorId(idJuego).isPresent();
            if (!existeJuego) {
                ArrayList<ErrorDto> err = new ArrayList<>();
                err.add(new ErrorDto("juego", ErrorTipo.NO_ENCONTRADO));
                throw new FormularioInvalidoException(err);
            }

            JuegoDTO juegoDTO = juegoRepo.obtenerPorId(idJuego).map(JuegoMapper::paraDTO).orElse(null);

            for (ResenaEntidad r : resenaRepo.obtenerTodos()) {
                if (r.getJuegoId().equals(idJuego) && r.getEstadoResena() == EstadoResena.PUBLICADA) {
                    UsuarioDTO usuarioDTO = usuarioRepo.obtenerPorId(r.getUsuarioId()).map(UsuarioMapper::paraDTO).orElse(null);
                    resultado.add(ResenaMapper.paraDTO(r, usuarioDTO, juegoDTO));
                }
            }
            return resultado;
        });
    }

    /**
     * Consulta y devuelve el catálogo de reseñas de un juego permitiendo aplicar filtros por estado.
     * <p>
     * En su implementación actual, este método actúa como un puente directo hacia
     * {@link #listarResenasJuego(Long)}, abstrayendo temporalmente el parámetro {@code filtroEstado}
     * y devolviendo la colección de reseñas que se encuentran publicadas en el sistema.
     * </p>
     *
     * @param idJuego      Identificador único del juego cuyas reseñas se van a consultar.
     * @param filtroEstado Criterio o estado específico para filtrar las reseñas (reservado para futuras versiones).
     * @return Una {@link java.util.List} de {@link ResenaDTO} con las reseñas asociadas al juego solicitado.
     * @throws FormularioInvalidoException Si el identificador del juego no se encuentra registrado en el sistema.
     */
    public java.util.List<ResenaDTO> verResenasJuego(Long idJuego, String filtroEstado) throws FormularioInvalidoException {
        return listarResenasJuego(idJuego);
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
    public List<ResenaDTO> listarResenasPorUsuario(Long idUsuario) throws FormularioInvalidoException {
        return tm.inTransaction(() -> {
            List<ResenaDTO> resultado = new ArrayList<>();

            boolean existeUsuario = usuarioRepo.obtenerPorId(idUsuario).isPresent();
            if (!existeUsuario) {
                ArrayList<ErrorDto> err = new ArrayList<>();
                err.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
                throw new FormularioInvalidoException(err);
            }

            UsuarioDTO usuarioDTO = usuarioRepo.obtenerPorId(idUsuario).map(UsuarioMapper::paraDTO).orElse(null);

            for (ResenaEntidad r : resenaRepo.obtenerTodos()) {
                if (r.getUsuarioId().equals(idUsuario)) {
                    if (r.getEstadoResena() != EstadoResena.ELIMINADA) {
                        JuegoDTO juegoDTO = juegoRepo.obtenerPorId(r.getJuegoId()).map(JuegoMapper::paraDTO).orElse(null);
                        resultado.add(ResenaMapper.paraDTO(r, usuarioDTO, juegoDTO));
                    }
                }
            }
            return resultado;
        });
    }

    public List<ResenaDTO> verResenasUsuario(Long idUsuario) throws FormularioInvalidoException {
        return listarResenasPorUsuario(idUsuario);
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
    public void ocultarResena(Long idResena, Long idUsuario) throws FormularioInvalidoException {
        tm.inTransaction(() -> {
            ResenaEntidad resena = resenaRepo.obtenerPorId(idResena).orElseThrow(() -> {
                ArrayList<ErrorDto> err = new ArrayList<>();
                err.add(new ErrorDto("resena", ErrorTipo.NO_ENCONTRADO));
                return new FormularioInvalidoException(err);
            });

            if (!resena.getUsuarioId().equals(idUsuario)) {
                ArrayList<ErrorDto> err = new ArrayList<>();
                err.add(new ErrorDto("usuario", ErrorTipo.NO_PROPIETARIO));
                throw new FormularioInvalidoException(err);
            }

            ResenaForm formOcultar = new ResenaForm(resena.getUsuarioId(), resena.getJuegoId(), resena.getRecomendado(), resena.getTextoResena(), resena.getHorasJuegoResena(), EstadoResena.OCULTA);

            resenaRepo.actualizar(idResena, formOcultar);

            return null;
        });
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
    public ResenaDTO eliminarResena(Long idResena, Long idUsuario) throws FormularioInvalidoException {
        return tm.inTransaction(() -> {
            ResenaEntidad resena = resenaRepo.obtenerPorId(idResena).orElseThrow(() -> {
                ArrayList<ErrorDto> err = new ArrayList<>();
                err.add(new ErrorDto("resena", ErrorTipo.NO_ENCONTRADO));
                return new FormularioInvalidoException(err);
            });

            if (!resena.getUsuarioId().equals(idUsuario)) {
                ArrayList<ErrorDto> err = new ArrayList<>();
                err.add(new ErrorDto("usuario", ErrorTipo.NO_PROPIETARIO));
                throw new FormularioInvalidoException(err);
            }
            ResenaForm formEliminar = new ResenaForm(resena.getUsuarioId(), resena.getJuegoId(), resena.getRecomendado(), resena.getTextoResena(), resena.getHorasJuegoResena(), EstadoResena.ELIMINADA);

            ResenaEntidad resenaActualizada = resenaRepo.actualizar(idResena, formEliminar).orElseThrow(() -> new FormularioInvalidoException((ArrayList<ErrorDto>) List.of(new ErrorDto("resenaNoEliminada", ErrorTipo.NO_ACTUALIZADO))));

            UsuarioDTO usuarioDTO = UsuarioMapper.paraDTO(usuarioRepo.obtenerPorId(idUsuario).get());
            JuegoDTO juegoDTO = JuegoMapper.paraDTO(juegoRepo.obtenerPorId(resena.getJuegoId()).get());

            return ResenaMapper.paraDTO(resenaActualizada, usuarioDTO, juegoDTO);

        });
    }

}
