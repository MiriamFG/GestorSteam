package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.modelo.dto.BibliotecaDTO;
import org.example.modelo.enums.EstadoInstalacion;
import org.example.modelo.form.BibliotecaForm;
import org.example.modelo.form.ErrorDTO;
import org.example.modelo.form.ErrorTipo;
import org.example.repositorio.implementacion.BibliotecaRepoInMemory;
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
            throw new FormularioInvalidoException(errores);
        }

        var usuario = usuarioOpt.get();
        var juego = juegoOpt.get();

        boolean yaExiste = bibliotecaRepo.obtenerTodos().stream()
                .anyMatch(b ->
                        b.getUsuarioDTO().equals(idUsuario)) &&
                        b.getJuegoDTO().equals(idJuego));

        if(yaExiste){
            errores.add(new ErrorDTO("biblioteca", ErrorTipo.DUPLICADO));
        }

        boolean compraExiste = compraRepo.obtenerTodos().stream()
                .anyMatch(c ->
                        c.getUsuarioDTO().equals(idUsuario)) &&
                        c.getJuegoDTO().equals(idJuego));

        if(!compraExiste){
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
                .orElseThrow(()-> new RuntimeException("No se pudo crear la biblitoeca"));
    }

    public void eliminarJuego(Long idUsuario, long idJuego){

        var biblioOpt = bibliotecaRepo.obtenerTodos().stream()
                .filter(b -> b.getUsuarioDTO().equals(idUsuario)) && b.getJuegoDTO().equals(idJuego))
                .findFirst();

        if(biblioOpt.isEmpty()){
            throw new RuntimeException("El juego no está en la biblioteca");
        }

        bibliotecaRepo.eliminar(biblioOpt.get().getId());

    }

}
