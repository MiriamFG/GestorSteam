package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.mapper.BibliotecaMapper;
import org.example.modelo.dto.BibliotecaDTO;
import org.example.modelo.dto.JuegoDTO;
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


    public List<BibliotecaDTO> verBibliotecaPersonal(Long idUsuario, String orden) throws FormularioInvalidoException{
        ArrayList<ErrorDTO> errores = new ArrayList<>();


        var usuarioOpt = usuarioRepo.obtenerPorId(idUsuario);
        if(usuarioOpt.isEmpty()){
            errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
        }

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
                    bibliotecaUsuario.sort((b1, b2) -> b1.getNumHorasTotal()
                            .compareTo(b2.getNumHorasTotal()));
                    break;
                case "ultimasesion":
                    bibliotecaUsuario.sort((b1, b2) -> {
                        LocalDate f1 = b1.getUltimaFechaJuego() != null ? b1.getUltimaFechaJuego() : LocalDate.MIN;
                        LocalDate f2 = b2.getUltimaFechaJuego() != null ? b2.getUltimaFechaJuego() : LocalDate.MIN;
                        return f1.compareTo(f2);
                    });
                    break;
                case "fechaadquisicion":
                    bibliotecaUsuario.sort((b1, b2) -> b1.getFechaAdquisicion()
                            .compareTo(b2.getFechaAdquisicion()));
                    break;
            }
        }

        return bibliotecaUsuario;

    }

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
                buscarRegistro = true;
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

    public String consultarUltimaSesion(Long idUsuario, Long idJuego) throws FormularioInvalidoException {
        List<ErrorDTO> errores = new ArrayList<>();

        BibliotecaEntidad registro = null;
        for (BibliotecaEntidad b : bibliotecaRepo.obtenerTodos()) {
            if (b.getUsuarioId().equals(idUsuario) && b.getJuegoId().equals(idJuego)) {
                registro = b;
                break;
            }
        }

        if (registro == null) {
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.NO_ENCONTRADO));
        }

        LocalDate ultimaSesion = registro.getUltimaFechaJuego();
        if (ultimaSesion == null) {
            return "Última sesión: Nunca jugado";
        }

        LocalDate hoy = LocalDate.now();
        long dias = java.time.temporal.ChronoUnit.DAYS.between(ultimaSesion, hoy);
        String fechaFormateada = ultimaSesion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return String.format("Última sesión: hace %d días (%s)", dias, fechaFormateada);
    }

}
