package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.mapper.BibliotecaMapper;
import org.example.mapper.JuegoMapper;
import org.example.mapper.UsuarioMapper;
import org.example.modelo.dto.*;
import org.example.modelo.entidad.BibliotecaEntidad;
import org.example.modelo.enums.EstadoInstalacion;
import org.example.modelo.form.BibliotecaForm;
import org.example.modelo.form.ErrorDTO;
import org.example.modelo.form.ErrorTipo;
import org.example.repositorio.interfaces.IBibliotecaRepo;
import org.example.repositorio.interfaces.ICompraRepo;
import org.example.repositorio.interfaces.IJuegoRepo;
import org.example.repositorio.interfaces.IUsuarioRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BibliotecaControlador {

    private final IUsuarioRepo usuarioRepo;
    private final IJuegoRepo juegoRepo;
    private final IBibliotecaRepo bibliotecaRepo;
    private final ICompraRepo compraRepo;

    public BibliotecaControlador(IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo,
                                 IBibliotecaRepo bibliotecaRepo, ICompraRepo compraRepo) {

        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
        this.bibliotecaRepo = bibliotecaRepo;
        this.compraRepo = compraRepo;
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
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        var usuarioOpt = usuarioRepo.obtenerPorId(idUsuario);
        if (usuarioOpt.isEmpty()) {
            errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
        }

        if (!errores.isEmpty()) throw new FormularioInvalidoException(errores);

        List<BibliotecaDTO> bibliotecaUsuario = bibliotecaRepo.obtenerTodos().stream()
                .filter(b -> b.getUsuarioId().equals(idUsuario))
                .map(entidad -> {
                    UsuarioDTO u = usuarioRepo.obtenerPorId(entidad.getUsuarioId()).map(UsuarioMapper::paraDTO).orElse(null);
                    JuegoDTO j = juegoRepo.obtenerPorId(entidad.getJuegoId()).map(JuegoMapper::paraDTO).orElse(null);
                    return new BibliotecaDTO(
                            entidad.getId(),
                            u,
                            entidad.getUsuarioId(),
                            j,
                            entidad.getJuegoId(),
                            entidad.getFechaAdquisicion(),
                            entidad.getNumHorasTotal(),
                            entidad.getUltimaFechaJuego(),
                            entidad.getEstadoInstalacion()
                    );
                })
                .toList();

        if (orden != null) {
            switch (orden.toLowerCase()) {
                case "alfabetico":
                    bibliotecaUsuario.sort((b1, b2) -> b1.getJuegoDTO().getTitulo()
                            .compareToIgnoreCase(b2.getJuegoDTO().getTitulo()));
                    break;
                case "tiempo":
                    bibliotecaUsuario.sort(Comparator.comparing(BibliotecaDTO::getNumHorasTotal));
                    break;
                case "ultimasesion":
                    bibliotecaUsuario.sort((b1, b2) -> {
                        LocalDateTime f1 = b1.getUltimaFechaJuego() != null ? b1.getUltimaFechaJuego() : LocalDateTime.MIN;
                        LocalDateTime f2 = b2.getUltimaFechaJuego() != null ? b2.getUltimaFechaJuego() : LocalDateTime.MIN;
                        return f1.compareTo(f2);
                    });
                    break;
                case "fechaadquisicion":
                    bibliotecaUsuario.sort(Comparator.comparing(BibliotecaDTO::getFechaAdquisicion));
                    break;
                default:
                    throw new IllegalArgumentException("Opción no encontrada");
            }
        }
        return bibliotecaUsuario;
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
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        if (idUsuario == null || idJuego == null) {
            if (idUsuario == null) errores.add(new ErrorDTO("usuario", ErrorTipo.REQUERIDO));
            if (idJuego == null) errores.add(new ErrorDTO("juego", ErrorTipo.REQUERIDO));
            throw new FormularioInvalidoException(errores);
        }

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException(errores);
        }

        var usuarioOpt = usuarioRepo.obtenerPorId(idUsuario);
        if (usuarioOpt.isEmpty()) {
            errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
        }

        var juegoOpt = juegoRepo.obtenerPorId(idJuego);
        if (juegoOpt.isEmpty()) {
            errores.add(new ErrorDTO("juego", ErrorTipo.NO_ENCONTRADO));
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
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.DUPLICADO));
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

        var entidad = bibliotecaRepo.crear(form)
                .orElseThrow(() -> {
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });

        return BibliotecaMapper.paraDTO(entidad);

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
    public void eliminarJuego(Long idUsuario, Long idJuego) throws FormularioInvalidoException {
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        if (idUsuario == null || idJuego == null) {
            if (idUsuario == null) errores.add(new ErrorDTO("usuario", ErrorTipo.REQUERIDO));
            if (idJuego == null) errores.add(new ErrorDTO("juego", ErrorTipo.REQUERIDO));
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
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.NO_ENCONTRADO));
            throw new FormularioInvalidoException(errores);
        }

        boolean eliminado = bibliotecaRepo.eliminar(registro.getId());
        if (!eliminado) {
            throw new FormularioInvalidoException(errores);
        }
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
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        BibliotecaEntidad registroBiblio = null;

        for (var b : bibliotecaRepo.obtenerTodos()) {
            if (b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)) {
                registroBiblio = b;
                break;
            }
        }

        if (registroBiblio == null) {
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.NO_ENCONTRADO));
            throw new FormularioInvalidoException(errores);
        }

        if (horasASumar < 0) {
            errores.add(new ErrorDTO("numHorasTotal", ErrorTipo.VALOR_DEMASIADO_BAJO));
        }

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException((ArrayList<ErrorDTO>) errores);
        }

        var formActualizado = new BibliotecaForm(
                registroBiblio.getUsuarioId(),
                registroBiblio.getJuegoId(),
                registroBiblio.getFechaAdquisicion(),
                (double) (registroBiblio.getNumHorasTotal() + horasASumar),
                LocalDateTime.now(),
                registroBiblio.getEstadoInstalacion()
        );

        var actualizado = bibliotecaRepo.actualizar(registroBiblio.getId(), formActualizado)
                .orElseThrow(() -> {
                    errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
                    return new FormularioInvalidoException(errores);
                });

        return BibliotecaMapper.paraDTO(actualizado);
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
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        BibliotecaEntidad registro = null;
        for (BibliotecaEntidad b : bibliotecaRepo.obtenerTodos()) {
            if (b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)) {
                registro = b;
                break;
            }
        }

        if (registro == null) {
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.NO_ENCONTRADO));
            throw new FormularioInvalidoException(errores);
        }

        if (registro.getUltimaFechaJuego() == null) {
            return new SesionInfoDTO(null, null, true);
        }

        LocalDateTime ultimaSesion = registro.getUltimaFechaJuego();
        LocalDateTime ahora = LocalDateTime.now();
        long dias = java.time.temporal.ChronoUnit.DAYS.between(ultimaSesion, ahora);

        return new SesionInfoDTO(ultimaSesion.toLocalDate(), dias, false);
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
    public EstadisticasBiblioDTO consultarEstadisticas(Long idUsuario) {
        int totalJuegos = 0;
        int horasTotales = 0;
        int juegosInstalados = 0;
        double valorTotalBiblioteca = 0.0;
        int juegosNuncaJugados = 0;
        int maxHoras = -1;
        String juegoMasJuegado = "Ninguno";

        for (BibliotecaEntidad registro : bibliotecaRepo.obtenerTodos()) {
            if (registro.getUsuarioId().equals(idUsuario)) {
                totalJuegos++;
                horasTotales += registro.getNumHorasTotal();

                if (registro.getEstadoInstalacion() == EstadoInstalacion.INSTALADO) {
                    juegosInstalados++;
                }

                if (registro.getNumHorasTotal() == 0) {
                    juegosNuncaJugados++;
                }

                if (registro.getNumHorasTotal() > maxHoras) {
                    maxHoras = registro.getNumHorasTotal();

                    var juego = juegoRepo.obtenerPorId(registro.getJuegoId());
                    if (juego.isPresent()) {
                        juegoMasJuegado = juego.get().getTitulo();
                    }
                }

                var juegoParaPrecio = juegoRepo.obtenerPorId(registro.getJuegoId());
                if (juegoParaPrecio.isPresent()) {
                    valorTotalBiblioteca += juegoParaPrecio.get().getPrecioBase();
                }
            }
        }
        return new EstadisticasBiblioDTO(
                totalJuegos,
                horasTotales,
                juegosInstalados,
                juegoMasJuegado,
                valorTotalBiblioteca,
                juegosNuncaJugados
        );

    }
}
