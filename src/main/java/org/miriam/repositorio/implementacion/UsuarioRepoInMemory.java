package org.miriam.repositorio.implementacion;

import org.miriam.modelo.entidad.UsuarioEntidad;
import org.miriam.modelo.enums.EstadoCuenta;
import org.miriam.modelo.form.UsuarioForm;
import org.miriam.repositorio.interfaces.IUsuarioRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UsuarioRepoInMemory implements IUsuarioRepo {
    private static final List<UsuarioEntidad> USUARIOS = new ArrayList<>();
    private static Long idCount = 1L;

    @Override
    public Optional<UsuarioEntidad> crear(UsuarioForm form) {
        var usuario = new UsuarioEntidad(idCount++, form.getNombreUsuario(), form.getEmail(), form.getContrasena(), form.getNombreReal(), form.getPais(), form.getFechaNac(), LocalDateTime.now(), form.getAvatar(), 0d, EstadoCuenta.ACTIVA);
        USUARIOS.add(usuario);
        return Optional.of(usuario);
    }

    @Override
    public Optional<UsuarioEntidad> obtenerPorId(Long id) {
        return USUARIOS.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<UsuarioEntidad> obtenerTodos() {
        return new ArrayList<>(USUARIOS);
    }


    @Override
    public Optional<UsuarioEntidad> actualizar(Long id, UsuarioForm form) {
        return obtenerPorId(id).map(u -> {
            UsuarioEntidad actualizado = new UsuarioEntidad(
                    id,
                    form.getNombreUsuario(),
                    form.getEmail(),
                    form.getContrasena(),
                    form.getNombreReal(),
                    form.getPais(),
                    form.getFechaNac(),
                    u.getFechaReg(),
                    form.getAvatar(),
                    u.getSaldoCartera(),
                    u.getEstadoCuenta()
            );
            USUARIOS.removeIf(usuario -> usuario.getId().equals(id));
            USUARIOS.add(actualizado);
            return actualizado;
        });
    }

    @Override
    public boolean actualizarSoloSaldo(Long id, Double nuevoSaldo) {

        if (obtenerPorId(id).isPresent()){
            UsuarioEntidad antiguo = obtenerPorId(id).get();

            UsuarioEntidad actualizado = new UsuarioEntidad(
                    antiguo.getId(),
                    antiguo.getNombreUsuario(),
                    antiguo.getEmail(),
                    antiguo.getContrasena(),
                    antiguo.getNombreReal(),
                    antiguo.getPais(),
                    antiguo.getFechaNac(),
                    antiguo.getFechaReg(),
                    antiguo.getAvatar(),
                    nuevoSaldo,
                    antiguo.getEstadoCuenta()
            );

            USUARIOS.removeIf(usuario -> usuario.getId().equals(id));
            USUARIOS.add(actualizado);
            return !Objects.equals(
                    antiguo.getSaldoCartera(),
                    actualizado.getSaldoCartera()
            );
        }
        return false;
    }

    @Override
    public void actualizar(UsuarioEntidad usuario) {
        USUARIOS.removeIf(u -> u.getId().equals(usuario.getId()));
        USUARIOS.add(usuario);
    }

    @Override
    public boolean eliminar(Long id) {
        return USUARIOS.removeIf(usuario -> usuario.getId().equals(id));
    }

    @Override
    public Optional<UsuarioEntidad> obtenerPorNombre(String nombre) {

        return USUARIOS.stream()
                .filter(u -> u.getNombreUsuario().equalsIgnoreCase(nombre))
                .findFirst();
    }

    @Override
    public Optional<UsuarioEntidad> obtenerPorEmail(String email) {

        return USUARIOS.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public static void limpiarRepositorio() {

        USUARIOS.clear();

        idCount = 1L;
    }
}
