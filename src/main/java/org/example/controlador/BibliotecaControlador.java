package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.mapper.BibliotecaMapper;
import org.example.modelo.dto.BibliotecaDTO;
import org.example.modelo.dto.EstadisticasBiblioDTO;
import org.example.modelo.dto.SesionInfoDTO;
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

    public BibliotecaControlador(IUsuarioRepo usuarioRepo, IJuegoRepo juegoRepo, IBibliotecaRepo bibliotecaRepo, ICompraRepo compraRepo) {
        this.usuarioRepo = usuarioRepo;
        this.juegoRepo = juegoRepo;
        this.bibliotecaRepo = bibliotecaRepo;
        this.compraRepo = compraRepo;
    }


    /**
     * Recupera y organiza el catálogo de juegos adquiridos por un usuario
     *
     * Criterios de orden
     *  alfabetico: Ordena por el título del juego (A-Z)
     *  tiempo: Ordena de menor a mayor cantidad de horas jugadas
     *  ultimasesion: Ordena cronológicamente según la última vez que se ejecutó el juego
     *  fechaadquisicion: Ordena según el momento en que se realizó la compra.
     *
     * @param idUsuario Identificador único del usuario cuya biblioteca se desea consultar
     * @param orden Criterio de ordenación (alfabetico, tiempo, ultimasesion, fechaadquisicion)
     * @return una List de BibliotecaDTO ordenada según el criterio solicitado
     * @throws FormularioInvalidoException Si el identificador del usuario no corresponde a ninguna cuenta activa
     */
    public List<BibliotecaDTO> verBibliotecaPersonal(Long idUsuario, String orden) throws FormularioInvalidoException{
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        var usuarioOpt = usuarioRepo.obtenerPorId(idUsuario);
        if(usuarioOpt.isEmpty()){
            errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
        }

        if (!errores.isEmpty()) throw new FormularioInvalidoException(errores);

        List<BibliotecaDTO> bibliotecaUsuario = bibliotecaRepo.obtenerTodos().stream()
                .filter(b -> b.getUsuarioId().equals(idUsuario))
                .map(BibliotecaDTO::new)
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
                        LocalDate f1 = b1.getUltimaFechaJuego() != null ? b1.getUltimaFechaJuego() : LocalDate.MIN;
                        LocalDate f2 = b2.getUltimaFechaJuego() != null ? b2.getUltimaFechaJuego() : LocalDate.MIN;
                        return f1.compareTo(f2);
                    });
                    break;
                case "fechaadquisicion":
                    bibliotecaUsuario.sort(Comparator.comparing(BibliotecaDTO::getFechaAdquisicion));
                    break;
            }
        }
        return bibliotecaUsuario;
    }

    /**
     * Vincula un juego al usuario tras la compra válda
     *
     * Validaciones
     *  Existencia: confirma que el usuario y el juego existen en sistema
     *  Propiedad: verifica que existe un registro de compra
     *  Unucudad: evita la duplicidad de licencias en la biblioteca
     *
     * Tras las validaciones, inicializa el registro con 0 horas de juego y estado 'NO_INSTALADO'.
     *
     * @param idUsuario Identificador del usuario que recibe el juego
     * @param idJuego Identificador del juego a añadir
     * @return un BibliotecaDTO que representa la nueva entrada en la colección del usuario
     * @throws FormularioInvalidoException Si no se encuentra la compra, el juego ya existe
     * en la biblioteca o hay inconsistencias en las fechas
     */
    public BibliotecaDTO aniadirJuegosBiblioteca(Long idUsuario, Long idJuego) throws FormularioInvalidoException {
        ArrayList<ErrorDTO> errores = new ArrayList<>();

        var usuarioOpt = usuarioRepo.obtenerPorId(idUsuario);
        if(usuarioOpt.isEmpty()){
            errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
        }

        var juegoOpt = juegoRepo.obtenerPorId(idJuego);
        if(juegoOpt.isEmpty()){
            errores.add(new ErrorDTO("juego", ErrorTipo.NO_ENCONTRADO));
        }

        if(!errores.isEmpty()){
            throw new FormularioInvalidoException(errores);
        }

        var usuario = usuarioOpt.get();

        boolean duplicadoBiblioteca = false;
        for(var b : bibliotecaRepo.obtenerTodos()){
            if(b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)){
                duplicadoBiblioteca = true;
                break;
            }
        }

        if(duplicadoBiblioteca){
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.DUPLICADO));
        }


        boolean compraEncontrada = false;
        for(var c : compraRepo.obtenerTodos()){
            if(c.getUsuarioId().equals(idUsuario) && c.getJuegoId().equals(idJuego)){
                compraEncontrada = true;
                break;
            }
        }
        if(!compraEncontrada){
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.NO_ENCONTRADO));
        }


        var fechaAdqusicion  = LocalDateTime.now();
        if(fechaAdqusicion.isBefore(usuario.getFechaReg())){
            errores.add(new ErrorDTO("fechaAdquisicion", ErrorTipo.FECHA_INVALIDA));
        }

        if(!errores.isEmpty()){
            throw new FormularioInvalidoException(errores);
        }

        var form = new BibliotecaForm(
                idUsuario,
                idJuego,
                fechaAdqusicion,
                0,
                null,
                EstadoInstalacion.NO_INSTALADO
        );

        var entidad = bibliotecaRepo.crear(form)
                .orElseThrow(()-> new RuntimeException("No se pudo crear la biblioteca"));

        return BibliotecaMapper.paraDTO(entidad);

    }

    /**
     * Elimina de forma definitiva un videojuego de la biblioteca personal de un usuario
     *
     * Acciones
     *  Localiza el registro por ID
     *  Si el usuario no posee el juego, lanza una excepción controlada
     *  Solicita al repositorio la eliminación del registro
     *  Verifica que la operación de haya completado correctamente
     * @param idUsuario Identificador del propietario de la biblioteca
     * @param idJuego Identificador del juego que se desea remover
     * @throws FormularioInvalidoException Si el juego no existe en la biblioteca del usuario
     * @throws RuntimeException Si ocurre un error inesperado durante la persistencia en el repositorio
     */
    public void eliminarJuego(Long idUsuario, long idJuego) throws FormularioInvalidoException{
        List<ErrorDTO> errores = new ArrayList<>();

        BibliotecaEntidad registro = null;
        for(BibliotecaEntidad b : bibliotecaRepo.obtenerTodos()){
            if(b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)){
                registro = b;
                break;
            }
        }

        if (registro == null) {
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.NO_ENCONTRADO));
        }

        if (!errores.isEmpty()) {
            throw new FormularioInvalidoException((ArrayList<ErrorDTO>) errores);
        }

        boolean eliminado = bibliotecaRepo.eliminar(registro.getId());
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar el juego de la biblioteca");
        }
    }

    /**
     * Registra una sesión de juego, incrementando el tiempo total y actualizando la fecha
     *
     * Verifica la existencia del usuario, del juego y de la licencia en la biblioteca
     * Asegura que el incremento de tiempo sea un valor positivo
     * Suma las nuevas horas al contador acumulado y actualiza ultimaFechaJuego al dia actual
     *
     * @param idUsuario Identificador del jugador
     * @param idJuego Identificador del título ejecutado
     * @param horasASumar Cantidad de horas a añadir al contador global
     * @return un BibliotecaDTO con las estadísticas de tiempo y fecha de sesión actualizadas
     * @throws FormularioInvalidoException Si los IDs no son válidos, si el usuario no posee el juego
     * o si la cantidad de horas es igual o menor a cero
     * @throws RuntimeException Si ocurre un fallo técnico durante la actualización en el repositorio
     */
    public BibliotecaDTO actualizarTiempoJuego(Long idUsuario, Long idJuego, int horasASumar) throws FormularioInvalidoException {
        List<ErrorDTO> errores = new ArrayList<>();

        var usuarioOpt = usuarioRepo.obtenerPorId(idUsuario);
        if(usuarioOpt.isEmpty()){
            errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
        }

        var juegoOpt = juegoRepo.obtenerPorId(idJuego);
        if(juegoOpt.isEmpty()){
            errores.add(new ErrorDTO("juego", ErrorTipo.NO_ENCONTRADO));
        }

        BibliotecaEntidad registroBiblio = null;

        boolean buscarRegistro = false;
        for(var b : bibliotecaRepo.obtenerTodos()){
            if(b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)){
                registroBiblio = b;
                break;
            }
        }

        if(registroBiblio == null){
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.NO_ENCONTRADO));
        }

        if(horasASumar <= 0){
            errores.add(new ErrorDTO("numHorasTotal", ErrorTipo.VALOR_DEMASIADO_BAJO));
        }

        if(!errores.isEmpty()){
            throw new FormularioInvalidoException((ArrayList<ErrorDTO>) errores);
        }

        var formActualizado = new BibliotecaForm(
                registroBiblio.getUsuarioId(),
                registroBiblio.getJuegoId(),
                registroBiblio.getFechaAdquisicion(),
                registroBiblio.getNumHorasTotal() + horasASumar,
                LocalDate.now(),
                registroBiblio.getEstadoInstalacion()
        );

        var actualizado = bibliotecaRepo.actualizar(registroBiblio.getId(), formActualizado)
                .orElseThrow(()-> new RuntimeException("No se ha podido actualizar la biblioteca"));

        return BibliotecaMapper.paraDTO(actualizado);
    }

    /**
     * Recupera la información sobre la última vez que el usuario ejecutó un juego
     *
     * Confima que el usuario tenga el titulo del juego en su biblioteca
     * Detremina el numero de dias que han pasado desde la ultima sesión hasta la fecha actual
     * Devuelve el objeto SesionInfoDTO para que la capa de la vista gestione formato, idioma y estilo del mensaje.
     *
     * @param idUsuario Identificador único del usuario
     * @param idJuego Identificador único del juego
     * @return objeto SesionInfoDTO que encapsula fecham dias que han pasado y si el titulo ha sido iniciado alguna vez
     * @throws FormularioInvalidoException si no existe un registro
     */
    public SesionInfoDTO consultarUltimaSesion(Long idUsuario, Long idJuego) throws FormularioInvalidoException {
        List<ErrorDTO> errores = new ArrayList<>();

        BibliotecaEntidad registro = null;
        for (BibliotecaEntidad b : bibliotecaRepo.obtenerTodos()) {
            if (b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)) {
                registro = b;
                break;
            }
        }

        if (registro == null) {
            throw new FormularioInvalidoException(new ArrayList<>(List.of(
                    new ErrorDTO("biblioteca", ErrorTipo.NO_ENCONTRADO))));
        }

        if (registro.getUltimaFechaJuego() == null) {
            return new SesionInfoDTO(null, null, true);
        }

        LocalDate ultimaSesion = registro.getUltimaFechaJuego();
        long dias = java.time.temporal.ChronoUnit.DAYS.between(ultimaSesion, LocalDate.now());

        return new SesionInfoDTO(ultimaSesion, dias, false);
    }

    /**
     * Genera informe con las estadisticas
     *
     *El metodo junga datos de varias fuentes para caluclar
     *  la cantidad total de titulos y juegos que tiene instalados el usuario
     *  el total de horas jugadas y el titulo de juego más usado
     *  valor total de inversion según los precios base del catálogo
     *  que juegos se han comprado pero nunca se han abierto
     *
     * @param idUsuario Identificador del usuario para el cual se generan las estadísticas
     * @return EstadisticasBiblioDTO con el resumen ejecutivo de la biblioteca
     */
    public EstadisticasBiblioDTO consultarEstadisticas(Long idUsuario){
        int totalJuegos = 0;
        int horasTotales = 0;
        int juegosInstalados = 0;
        double valorTotalBiblioteca= 0.0;
        int juegosNuncaJugados= 0;
        int maxHoras = -1;
        String juegoMasJuegado = "Ninguno";

        for(BibliotecaEntidad registro : bibliotecaRepo.obtenerTodos()){
            if(registro.getUsuarioId().equals(idUsuario)){
                totalJuegos++;
                horasTotales += registro.getNumHorasTotal();

                if(registro.getEstadoInstalacion() ==EstadoInstalacion.INSTALADO){
                    juegosInstalados++;
                }

                if(registro.getNumHorasTotal() == 0){
                    juegosNuncaJugados++;
                }

                if(registro.getNumHorasTotal() > maxHoras){
                    maxHoras = registro.getNumHorasTotal();

                    var juego = juegoRepo.obtenerPorId(registro.getJuegoId());
                    if(juego.isPresent()){
                        juegoMasJuegado = juego.get().getTitulo();
                    }
                }

                var juegoParaPrecio = juegoRepo.obtenerPorId(registro.getJuegoId());
                if(juegoParaPrecio.isPresent()){
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
