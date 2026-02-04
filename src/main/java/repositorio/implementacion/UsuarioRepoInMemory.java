package repositorio.implementacion;

import modelo.entidad.UsuarioEntidad;
import modelo.form.UsuarioForm;
import repositorio.interfaces.IUsuarioRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepoInMemory implements IUsuarioRepo {
    private static final List<UsuarioEntidad> usuarios = new ArrayList<>();
    private static Long idCount = 1L;

    @Override
    public Optional<UsuarioEntidad> crear(UsuarioForm form) {
         var usuario = new UsuarioEntidad(idCount++, form.nombreUsuario, form.email, form.contrasena, form.nombreReal, form.pais, form.fechaNac, form.fechaReg, form.avatar, form.saldoCartera, form.estadoCuenta);
        usuarios.add(usuario);
         return Optional.of(usuario);
    }

    @Override
    public Optional<UsuarioEntidad> obtenerPorId(Long id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<UsuarioEntidad> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }

    @Override
    public Optional<UsuarioEntidad> actualizar(Long aLong, UsuarioForm dto) {
        return Optional.empty();
    }

    @Override
    public boolean eliminar(Long aLong) {
        return false;
    }
}
