package org.miriam.controlador;

import org.miriam.excepciones.FormularioInvalidoException;
import org.miriam.mapper.BibliotecaMapper;
import org.miriam.mapper.JuegoMapper;
import org.miriam.mapper.UsuarioMapper;
import org.miriam.modelo.dto.*;
import org.miriam.modelo.entidad.BibliotecaEntidad;
import org.miriam.modelo.entidad.JuegoEntidad;
import org.miriam.modelo.entidad.UsuarioEntidad;
import org.miriam.modelo.enums.EstadoInstalacion;
import org.miriam.modelo.form.BibliotecaForm;
import org.miriam.modelo.form.ErrorDto;
import org.miriam.modelo.form.ErrorTipo;
import org.miriam.repositorio.interfaces.IBibliotecaRepo;
import org.miriam.repositorio.interfaces.IJuegoRepo;
import org.miriam.repositorio.interfaces.IUsuarioRepo;
import org.miriam.transaction.ITransactionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BibliotecaControlador {

    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;
    private final IBibliotecaRepo bibliotecaRepo;

    public ITransactionManager tm;


    public BibliotecaControlador(IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo,
                                 IBibliotecaRepo bibliotecaRepo, ITransactionManager tm) {

        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
        this.bibliotecaRepo = bibliotecaRepo;
        this.tm = tm;
    }

    /**
     * Recupera y organiza el catálogo de juegos adquiridos por un usuario.
     * <p>
     * Criterios de orden:
     * alfabetico: Ordena por el título del juego (A-Z),
     * tiempo: Ordena de menor a mayor cantidad de horas jugadas,
     * ultimasesion: Ordena cronológicamente según la última vez que se ejecutó el juego,
     * fechaadquisicion: Ordena según el momento en que se realizó la compra.
     *
     * @param idUsuario Identificador único del usuario cuya biblioteca se desea consultar.
     * @param orden     Criterio de ordenación (alfabetico, tiempo, ultimasesion, fechaadquisicion).
     * @return una List de BibliotecaDTO ordenada según el criterio solicitado.
     * @throws FormularioInvalidoException Si el identificador del usuario no corresponde a ninguna cuenta activa.
     */
    public List<BibliotecaDTO> verBibliotecaPersonal(Long idUsuario, String orden) throws FormularioInvalidoException {

        return tm.inTransaction(() -> {

            UsuarioEntidad usuarioEntidad =
                    usuarioRepo.obtenerPorId(idUsuario)
                            .orElseThrow(() -> {

                                ArrayList<ErrorDto> errores =
                                        new ArrayList<>();

                                errores.add(
                                        new ErrorDto(
                                                "usuario",
                                                ErrorTipo.NO_ENCONTRADO
                                        )
                                );

                                return new FormularioInvalidoException(
                                        errores
                                );
                            });

            UsuarioDTO usuarioDto =
                    UsuarioMapper.paraDTO(usuarioEntidad);

            Comparator<BibliotecaDTO> comparator =
                    obtenerCriterioOrdenacion(orden);

            java.util.Map<Long, JuegoDTO> mapaJuegos =
                    juegoRepo.obtenerTodos().stream()
                            .collect(java.util.stream.Collectors.toMap(
                                    JuegoEntidad::getId,
                                    JuegoMapper::paraDTO,
                                    (existente, reemplazo) -> existente
                            ));

            List<BibliotecaDTO> biblioteca = bibliotecaRepo.obtenerTodos()
                    .stream()
                    .filter(b -> b.getUsuarioId().equals(idUsuario))
                    .map(entidad -> {

                        JuegoDTO juegoDto =
                                mapaJuegos.get(entidad.getJuegoId());

                        return new BibliotecaDTO(
                                entidad.getId(),
                                usuarioDto,
                                entidad.getUsuarioId(),
                                juegoDto,
                                entidad.getJuegoId(),
                                entidad.getFechaAdquisicion(),
                                entidad.getNumHorasTotal(),
                                entidad.getUltimaFechaJuego(),
                                entidad.getEstadoInstalacion()
                        );
                    })
                    .sorted(comparator)
                    .toList();

            return biblioteca;
        });
    }

    private Comparator<BibliotecaDTO> obtenerCriterioOrdenacion(String orden) {
        if (orden == null) {
            return (b1, b2) -> 0;
        }

        return switch (orden.toLowerCase()) {
            case "alfabetico" -> Comparator.comparing(
                    b -> b.getJuegoDTO() != null ? b.getJuegoDTO().getTitulo() : "",
                    String.CASE_INSENSITIVE_ORDER
            );
            case "tiempo" -> Comparator.comparing(BibliotecaDTO::getNumHorasTotal);
            case "ultimasesion" -> Comparator.comparing(
                    BibliotecaDTO::getUltimaFechaJuego,
                    Comparator.nullsFirst(LocalDateTime::compareTo)
            );
            case "fechaadquisicion" -> Comparator.comparing(
                    BibliotecaDTO::getFechaAdquisicion,
                    Comparator.nullsFirst(LocalDateTime::compareTo)
            );
            default -> throw new IllegalArgumentException("Opción de ordenación no válida: " + orden);
        };
    }

    /**
     * Vincula un juego al usuario tras la compra válda.
     * <p>
     * Validaciones:
     * Existencia: confirma que el usuario y el juego existen en sistema.
     * Propiedad: verifica que existe un registro de compra.
     * Unucudad: evita la duplicidad de licencias en la biblioteca.
     * <p>
     * Tras las validaciones, inicializa el registro con 0 horas de juego y estado 'NO_INSTALADO'.
     *
     * @param idUsuario Identificador del usuario que recibe el juego.
     * @param idJuego   Identificador del juego a añadir.
     * @return BibliotecaDTO que representa la nueva entrada en la colección del usuario.
     * @throws FormularioInvalidoException Si no se encuentra la compra, el juego ya existe
     *                                     en la biblioteca o hay inconsistencias en las fechas.
     */
    public BibliotecaDTO aniadirJuegosBiblioteca(Long idUsuario, Long idJuego) throws FormularioInvalidoException {
        return tm.inTransaction(() -> {
            ArrayList<ErrorDto> errores = new ArrayList<>();

            if (idUsuario == null || idJuego == null) {
                if (idUsuario == null) errores.add(new ErrorDto("usuario", ErrorTipo.REQUERIDO));
                if (idJuego == null) errores.add(new ErrorDto("juego", ErrorTipo.REQUERIDO));
                throw new FormularioInvalidoException(errores);
            }

            var usuarioOpt = usuarioRepo.obtenerPorId(idUsuario);
            if (usuarioOpt.isEmpty()) {
                errores.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
            }

            var juegoOpt = juegoRepo.obtenerPorId(idJuego);
            if (juegoOpt.isEmpty()) {
                errores.add(new ErrorDto("juego", ErrorTipo.NO_ENCONTRADO));
            }

            if (!errores.isEmpty()) {
                throw new FormularioInvalidoException(errores);
            }

            boolean duplicadoBiblioteca = false;
            for (var b : bibliotecaRepo.obtenerTodos()) {
                if (b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)) {
                    duplicadoBiblioteca = true;
                    break;
                }
            }
            if (duplicadoBiblioteca) {
                errores.add(new ErrorDto("biblioteca", ErrorTipo.DUPLICADO));
                throw new FormularioInvalidoException(errores);
            }

            var form = new BibliotecaForm(
                    idUsuario,
                    idJuego,
                    LocalDateTime.now(),
                    0.0,
                    null,
                    EstadoInstalacion.NO_INSTALADO
            );

            BibliotecaEntidad entidad = bibliotecaRepo.crear(form)
                    .orElseThrow(() -> {
                        errores.add(new ErrorDto("usuario", ErrorTipo.NO_ENCONTRADO));
                        return new IllegalArgumentException("Error al crear en biblioteca");
                    });

            UsuarioDTO u = usuarioRepo.obtenerPorId(entidad.getUsuarioId()).map(UsuarioMapper::paraDTO).orElse(null);
            JuegoDTO j = juegoRepo.obtenerPorId(entidad.getJuegoId()).map(JuegoMapper::paraDTO).orElse(null);

            return BibliotecaMapper.paraDTO(entidad, u, j);
        });
    }

    /**
     * Elimina de forma definitiva un videojuego de la biblioteca personal de un usuario.
     * <p>
     * Acciones:
     * Localiza el registro por ID,
     * Si el usuario no posee el juego, lanza una excepción controlada.
     * Solicita al repositorio la eliminación del registro.
     * Verifica que la operación de haya completado correctamente.
     *
     * @param idUsuario Identificador del propietario de la biblioteca.
     * @param idJuego   Identificador del juego que se desea remover.
     * @throws FormularioInvalidoException Si el juego no existe en la biblioteca del usuario.
     * @throws RuntimeException            Si ocurre un error inesperado durante la persistencia en el repositorio.
     */
    public void eliminarBiblioteca(Long idUsuario, Long idJuego) throws FormularioInvalidoException {

        tm.inTransaction(() -> {

            ArrayList<ErrorDto> errores = new ArrayList<>();

            if (idUsuario == null || idJuego == null) {
                if (idUsuario == null) errores.add(new ErrorDto("usuario", ErrorTipo.REQUERIDO));
                if (idJuego == null) errores.add(new ErrorDto("juego", ErrorTipo.REQUERIDO));
                throw new FormularioInvalidoException(errores);
            }

            BibliotecaEntidad registro = null;

            for (BibliotecaEntidad b : bibliotecaRepo.obtenerTodos()) {
                if (b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)) {
                    registro = b;
                    break;
                }
            }

            if (registro == null) {
                errores.add(new ErrorDto("biblioteca", ErrorTipo.NO_ENCONTRADO));
                throw new FormularioInvalidoException(errores);
            }

            final Long registroId = registro.getId();
            boolean eliminado = bibliotecaRepo.eliminar(registroId);

            if (!eliminado) {
                throw new IllegalArgumentException("Error al eliminar el juego de la biblioteca");
            }
            return null;
        });
    }

    /**
     * Registra una sesión de juego, incrementando el tiempo total y actualizando la fecha.
     * <p>
     * Verifica la existencia del usuario, del juego y de la licencia en la biblioteca.
     * Asegura que el incremento de tiempo sea un valor positivo.
     * Suma las nuevas horas al contador acumulado y actualiza ultimaFechaJuego al dia actual.
     *
     * @param idUsuario   Identificador del jugador.
     * @param idJuego     Identificador del título ejecutado.
     * @param horasASumar Cantidad de horas a añadir al contador global.
     * @return BibliotecaDTO con las estadísticas de tiempo y fecha de sesión actualizadas.
     * @throws FormularioInvalidoException Si los IDs no son válidos, si el usuario no posee el juego
     *                                     o si la cantidad de horas es igual o menor a cero.
     * @throws RuntimeException            Si ocurre un fallo técnico durante la actualización en el repositorio.
     */
    public BibliotecaDTO actualizarTiempoJuego(Long idUsuario, Long idJuego, int horasASumar) throws FormularioInvalidoException {
        return tm.inTransaction(() -> {
            ArrayList<ErrorDto> errores = new ArrayList<>();
            BibliotecaEntidad registroBiblio = null;

            for (var b : bibliotecaRepo.obtenerTodos()) {
                if (b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)) {
                    registroBiblio = b;
                    break;
                }
            }

            if (registroBiblio == null) {
                errores.add(new ErrorDto("biblioteca", ErrorTipo.NO_ENCONTRADO));
                throw new FormularioInvalidoException(errores);
            }

            if (horasASumar < 0) {
                errores.add(new ErrorDto("numHorasTotal", ErrorTipo.VALOR_DEMASIADO_BAJO));
            }

            if (!errores.isEmpty()) {
                throw new FormularioInvalidoException(new ArrayList<>(errores));
            }

            final BibliotecaEntidad registroFinal = registroBiblio;
            var formActualizado = new BibliotecaForm(
                    registroFinal.getUsuarioId(),
                    registroFinal.getJuegoId(),
                    registroFinal.getFechaAdquisicion(),
                    (double) (registroFinal.getNumHorasTotal() + horasASumar),
                    LocalDateTime.now(),
                    registroFinal.getEstadoInstalacion()
            );

            BibliotecaEntidad actualizado = bibliotecaRepo.actualizar(registroFinal.getId(), formActualizado)
                    .orElseThrow(() -> {
                        errores.add(new ErrorDto("biblioteca", ErrorTipo.NO_ACTUALIZADO));
                        return new IllegalArgumentException("Error al actualizar la biblioteca");
                    });

            UsuarioDTO u = usuarioRepo.obtenerPorId(actualizado.getUsuarioId()).map(UsuarioMapper::paraDTO).orElse(null);
            JuegoDTO j = juegoRepo.obtenerPorId(actualizado.getJuegoId()).map(JuegoMapper::paraDTO).orElse(null);

            return BibliotecaMapper.paraDTO(actualizado, u, j);
        });
    }

    /**
     * Recupera la información sobre la última vez que el usuario ejecutó un juego.
     * <p>
     * Confima que el usuario tenga el titulo del juego en su biblioteca.
     * Detremina el numero de dias que han pasado desde la ultima sesión hasta la fecha actual.
     * Devuelve el objeto SesionInfoDTO para que la capa de la vista gestione formato, idioma y estilo del mensaje.
     *
     * @param idUsuario Identificador único del usuario.
     * @param idJuego   Identificador único del juego.
     * @return objeto SesionInfoDTO que encapsula fecham dias que han pasado y si el titulo ha sido iniciado alguna vez.
     * @throws FormularioInvalidoException si no existe un registro.
     */
    public SesionInfoDTO consultarUltimaSesion(Long idUsuario, Long idJuego) throws FormularioInvalidoException {

        return tm.inTransaction(() -> {
            BibliotecaEntidad registro = bibliotecaRepo.obtenerTodos().stream()
                    .filter(b -> b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego))
                    .findFirst()
                    .orElseThrow(() -> {
                        ArrayList<ErrorDto> errores = new ArrayList<>();
                        errores.add(new ErrorDto("biblioteca", ErrorTipo.NO_ENCONTRADO));
                        return new FormularioInvalidoException(errores);
                    });

            if (registro.getUltimaFechaJuego() == null) {
                return new SesionInfoDTO(null, null, true);
            }

            LocalDateTime ultimaSesion = registro.getUltimaFechaJuego();

            long dias = java.time.temporal.ChronoUnit.DAYS.between(ultimaSesion, LocalDateTime.now());

            return new SesionInfoDTO(ultimaSesion.toLocalDate(), dias, false);
        });

    }

    /**
     * Genera informe con las estadisticas.
     * <p>
     * El metodo junta datos de varias fuentes para calcular
     * la cantidad total de titulos y juegos que tiene instalados el usuario,
     * el total de horas jugadas y el titulo de juego más usado,
     * valor total de inversion según los precios base del catálogo,
     * que juegos se han comprado pero nunca se han abierto.
     *
     * @param idUsuario Identificador del usuario para el cual se generan las estadísticas.
     * @return EstadisticasBiblioDTO con el resumen ejecutivo de la biblioteca.
     */
    public EstadisticasBiblioDTO consultarEstadisticas(Long idUsuario) throws FormularioInvalidoException {
        return tm.inTransaction(() -> {

            int totalJuegos = 0;
            double horasTotales = 0.0;
            int juegosInstalados = 0;
            double valorTotalBiblioteca = 0.0;
            int juegosNuncaJugados = 0;
            double maxHoras = -1;
            String juegoMasJugado = "SIN JUEGOS";

            List<BibliotecaEntidad> bibliotecaUsuario =
                    bibliotecaRepo.obtenerTodos().stream()
                            .filter(registro ->
                                    registro.getUsuarioId().equals(idUsuario))
                            .toList();

            // SI LA BIBLIOTECA ESTA VACIA
            if (bibliotecaUsuario.isEmpty()) {

                return new EstadisticasBiblioDTO(
                        0,
                        0,
                        0,
                        "SIN JUEGOS",
                        0.0,
                        0
                );
            }

            for (BibliotecaEntidad registro : bibliotecaUsuario) {

                totalJuegos++;

                horasTotales += registro.getNumHorasTotal();

                if (registro.getEstadoInstalacion()
                        == EstadoInstalacion.INSTALADO) {
                    juegosInstalados++;
                }

                if (registro.getNumHorasTotal() == 0) {
                    juegosNuncaJugados++;
                }

                if (registro.getNumHorasTotal() > maxHoras) {
                    maxHoras = registro.getNumHorasTotal();

                    var juego =
                            juegoRepo.obtenerPorId(registro.getJuegoId());

                    if (juego.isPresent()) {
                        juegoMasJugado = juego.get().getTitulo();
                    }
                }

                var juegoPrecio =
                        juegoRepo.obtenerPorId(registro.getJuegoId());

                if (juegoPrecio.isPresent()) {

                    valorTotalBiblioteca +=
                            juegoPrecio.get().getPrecioBase();
                }
            }

            return new EstadisticasBiblioDTO(
                    totalJuegos,
                    (int) horasTotales,
                    juegosInstalados,
                    juegoMasJugado,
                    valorTotalBiblioteca,
                    juegosNuncaJugados
            );
        });
    }
}
