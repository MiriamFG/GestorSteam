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
    public Optional<UsuarioEntidad> actualizar(Long aLong, UsuarioForm dto) {
        return Optional.empty();
    }

    @Override
    public Optional<UsuarioEntidad> actualizar(Long id, UsuarioForm form, Optional<Double> saldo) {
        var usuarioOpt = obtenerPorId(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var saldoNuevo = saldo.orElse(usuarioOpt.getSaldoCartera());

        var usuarioActualizado = new UsuarioEntidad(id, form.getNombreUsuario(), form.getEmail(), form.getContrasena(), form.getNombreReal(), form.getPais(), form.getFechaNac(), LocalDateTime.now(), form.getAvatar(), saldoNuevo, EstadoCuenta.ACTIVA);
        USUARIOS.removeIf(usuario -> usuario.getId().equals(id));
        USUARIOS.add(usuarioActualizado);
        return Optional.of(usuarioActualizado);
    }

    @Override
    public void actualizarSoloSaldo(Long id, Double nuevoSaldo) {
        obtenerPorId(id).ifPresent(u -> u.setSaldoCartera(nuevoSaldo));
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
