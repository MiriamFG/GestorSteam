package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.modelo.dto.BibliotecaDTO;
import org.example.modelo.enums.EstadoInstalacion;
import org.example.modelo.form.BibliotecaForm;
import org.example.modelo.form.ErrorDTO;
import org.example.modelo.form.ErrorTipo;
import org.example.repositorio.interfaces.IBibliotecaRepo;
import org.example.repositorio.interfaces.ICompraRepo;
import org.example.repositorio.interfaces.IJuegoRepo;
import org.example.repositorio.interfaces.IUsuarioRepo;

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

    public BibliotecaDTO aniadirJuegosBiblioteca(Long idUsuario, Long idJuego) throws FormularioInvalidoException {
        List<ErrorDTO> errores = new ArrayList<>();

        var usuarioOpt = usuarioRepo.obtenerPorId(idUsuario);
        if(usuarioOpt.isEmpty()){
            errores.add(new ErrorDTO("usuario", ErrorTipo.NO_ENCONTRADO));
        }

        var juegoOpt = juegoRepo.obtenerPorId(idJuego);
        if(juegoOpt.isEmpty()){
            errores.add(new ErrorDTO("juego", ErrorTipo.NO_ENCONTRADO));
        }

        if(!errores.isEmpty()){
            throw new FormularioInvalidoException((ArrayList<ErrorDTO>) errores);
        }

        var usuario = usuarioOpt.get();
        var juego = juegoOpt.get();

        boolean yaExisteUsuario = bibliotecaRepo.obtenerTodos().stream()
                .anyMatch(b ->
                        b.getUsuarioId().equals(idUsuario));

        boolean yaExisteJuego = bibliotecaRepo.obtenerTodos().stream()
                .anyMatch(b ->
                        b.getJuegoId().equals(idJuego));

        if(yaExisteUsuario && yaExisteJuego){
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.DUPLICADO));
        }

        boolean compraExisteUsuario = compraRepo.obtenerTodos().stream()
                .anyMatch(c ->
                        c.getUsuarioId().equals(idUsuario));

        boolean compraExisteJuego = compraRepo.obtenerTodos().stream()
                .anyMatch(c ->
                        c.getJuegoId().equals(idJuego));

        if(!(compraExisteUsuario && compraExisteJuego)){
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.NO_ENCONTRADO));
        }

        var fechaAdqusicion  = LocalDateTime.now();

        if(fechaAdqusicion.isBefore(usuario.getFechaReg())){
            errores.add(new ErrorDTO("fechaAdquisicion", ErrorTipo.FECHA_INVALIDA));
        }

        if(!errores.isEmpty()){
            throw new FormularioInvalidoException((ArrayList<ErrorDTO>) errores);
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
    }

    public void eliminarJuego(Long idUsuario, long idJuego){
        var biblioOptUsuario = bibliotecaRepo.obtenerTodos().stream()
                .filter(b -> b.getUsuarioId().equals(idUsuario))
                .findFirst();

        var biblioOptJuego = bibliotecaRepo.obtenerTodos().stream()
                .filter(b -> b.getJuegoId().equals(idJuego))
                .findFirst();

        if(biblioOptUsuario.isEmpty()) {
            throw new RuntimeException("El usuario no está en la biblioteca");
        }

        if(biblioOptJuego.isEmpty()){
            throw new RuntimeException("El juego no está en la biblioteca");
        }
    }
}
