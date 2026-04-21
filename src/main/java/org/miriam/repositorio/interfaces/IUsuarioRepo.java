package org.miriam.repositorio.interfaces;

import org.miriam.modelo.entidad.UsuarioEntidad;
import org.miriam.modelo.form.UsuarioForm;

import java.util.Optional;

public interface IUsuarioRepo extends ICrud<UsuarioEntidad, UsuarioForm, Long> {

    Optional<UsuarioEntidad> obtenerPorNombre(String nombre);


    void actualizarSoloSaldo(Long id, Double nuevoSaldo);
}
