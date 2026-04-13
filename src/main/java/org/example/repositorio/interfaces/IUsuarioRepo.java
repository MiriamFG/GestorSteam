package org.example.repositorio.interfaces;

import org.example.modelo.entidad.UsuarioEntidad;
import org.example.modelo.form.UsuarioForm;

import java.util.Optional;

public interface IUsuarioRepo extends ICrud<UsuarioEntidad, UsuarioForm, Long> {

    Optional<UsuarioEntidad> obtenerPorNombre(String nombre);


    void actualizarSoloSaldo(Long id, Double nuevoSaldo);
}
