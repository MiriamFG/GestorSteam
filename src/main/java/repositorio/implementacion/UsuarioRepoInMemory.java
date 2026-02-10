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
         var usuario = new UsuarioEntidad(idCount++, form.getNombreUsuario(), form.getEmail(), form.getContrasena(), form.getNombreReal(), form.getPais(), form.getFechaNac(), form.getFechaReg(), form.getAvatar(), form.getSaldoCartera(), form.getEstadoCuenta());
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
    public Optional<UsuarioEntidad> actualizar(Long id, UsuarioForm form) {
        var usuarioOpt = obtenerPorId(id);
        if(usuarioOpt.isEmpty()){
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        var usuarioActualizado = new UsuarioEntidad(id,form.getNombreUsuario(), form.getEmail(), form.getContrasena(), form.getNombreReal(), form.getPais(), form.getFechaNac(), form.getFechaReg(), form.getAvatar(), form.getSaldoCartera(), form.getEstadoCuenta());
        usuarios.removeIf(usuario -> usuario.getId().equals(id));
        usuarios.add(usuarioActualizado);
        return Optional.of(usuarioActualizado);
    }

    @Override
    public boolean eliminar(Long id) {
        return usuarios.removeIf(usuario -> usuario.getId().equals(id));
    }
}
