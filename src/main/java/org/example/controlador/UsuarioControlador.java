package org.example.controlador;

import org.example.excepciones.FormularioInvalidoException;
import org.example.mapper.UsuarioMapper;
import org.example.modelo.dto.UsuarioDTO;
import org.example.modelo.entidad.UsuarioEntidad;
import org.example.modelo.enums.EstadoCuenta;
import org.example.modelo.form.ErrorDTO;
import org.example.modelo.form.ErrorTipo;
import org.example.modelo.form.UsuarioForm;
import org.example.repositorio.implementacion.PaisesRepoInMemory;
import org.example.repositorio.interfaces.IUsuarioRepo;

import java.time.LocalDate;
import java.util.ArrayList;

public class UsuarioControlador {

    private final IUsuarioRepo usuarioRepo;
    PaisesRepoInMemory paisRepo = new PaisesRepoInMemory();


    public UsuarioControlador(IUsuarioRepo usuarioRepo){
        this.usuarioRepo = usuarioRepo;
    }

    public UsuarioDTO registrarUsuario(UsuarioForm form) throws FormularioInvalidoException {

            var errores = new ArrayList<ErrorDTO>();

            for(UsuarioEntidad usuario : usuarioRepo.obtenerTodos()){

                if(form.getFechaNac().isAfter(LocalDate.now())){
                    errores.add(new ErrorDTO("fechaNac", ErrorTipo.FECHA_FUTURA));
                }

                boolean paisValido = paisRepo.obtenerTodos().stream().anyMatch(p -> p.equalsIgnoreCase(form.getPais()));
                if (!paisValido){
                    errores.add(new ErrorDTO("pais", ErrorTipo.NO_ENCONTRADO));
                }

                if(usuario.getNombreUsuario().equalsIgnoreCase(form.getNombreUsuario())){
                    errores.add(new ErrorDTO("usuario", ErrorTipo.EXISTENTE));
                }

                if(usuario.getEmail().equalsIgnoreCase(form.getEmail())){
                    errores.add(new ErrorDTO("email", ErrorTipo.REGISTRADO));
                }

                if(!errores.isEmpty()){
                    throw new FormularioInvalidoException(errores);
                }
            }

            UsuarioEntidad nuevoUsuario = usuarioRepo.crear(form)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no creado"));

            return UsuarioMapper.paraDTO(nuevoUsuario);
    }


    public UsuarioEntidad consultarPerfilPorNombre(String nombreUsuario){
        for(UsuarioEntidad usuario : usuarioRepo.obtenerTodos()){
            if(usuario.getNombreUsuario().equalsIgnoreCase(nombreUsuario)){
                return usuario;
            }
        }
        throw new IllegalArgumentException("Usuario no encontrado");
    }


    public Double aniadirSaldo(Long idUsuario, Double cantidad){
        var errores = new ArrayList<ErrorDTO>();

        UsuarioEntidad usuario = usuarioRepo.obtenerPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));


        if(usuario.getEstadoCuenta() != EstadoCuenta.ACTIVA){
            errores.add(new ErrorDTO("cuenta", ErrorTipo.NO_ACTIVO));
        }

        if(cantidad <= 0){
            errores.add(new ErrorDTO("saldo", ErrorTipo.VALOR_DEMASIADO_BAJO));
        }

        if(cantidad < 5.00 || cantidad > 500.00){
            errores.add(new ErrorDTO("saldo", ErrorTipo.LONGITUD_INVALIDA, 5.00, 500.00));

        }

        if(Math.round(cantidad *100)/ 100.0 != cantidad){
            errores.add(new ErrorDTO("saldo", ErrorTipo.MAX_DECIMALES, 2));

        }

        Double nuevoSaldo = usuario.getSaldoCartera() + cantidad;
        usuario.setSaldoCartera(nuevoSaldo);

        return nuevoSaldo;
    }

    public String consutarSaldo(Long idUsuario){
        UsuarioEntidad usuario = usuarioRepo.obtenerPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return String.format("%.2f €", usuario.getSaldoCartera());
    }

}
