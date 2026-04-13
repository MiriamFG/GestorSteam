package org.example.repositorio.implementacion;

import org.example.modelo.entidad.UsuarioEntidad;
import org.example.modelo.enums.EstadoCuenta;
import org.example.modelo.form.UsuarioForm;
import org.example.repositorio.interfaces.IUsuarioRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
                    u.getSaldoCartera(), // Mantenemos el saldo que ya tenía
                    u.getEstadoCuenta()
            );
            USUARIOS.removeIf(usuario -> usuario.getId().equals(id));
            USUARIOS.add(actualizado);
            return actualizado;
        });
    }

    @Override
    public void actualizarSoloSaldo(Long id, Double nuevoSaldo) {
        obtenerPorId(id).ifPresent(u -> {
            // Creamos un nuevo objeto con los mismos datos pero el nuevo saldo
            UsuarioEntidad actualizado = new UsuarioEntidad(
                    u.getId(),
                    u.getNombreUsuario(),
                    u.getEmail(),
                    u.getContrasena(),
                    u.getNombreReal(),
                    u.getPais(),
                    u.getFechaNac(),
                    u.getFechaReg(),
                    u.getAvatar(),
                    nuevoSaldo,
                    u.getEstadoCuenta()
            );

            USUARIOS.removeIf(usuario -> usuario.getId().equals(id));
            USUARIOS.add(actualizado);
        });
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
}
