package org.miriam.repositorio.interfaces;

import org.miriam.modelo.entidad.JuegoEntidad;
import org.miriam.modelo.form.JuegoForm;

import java.util.Optional;

public interface IJuegoRepo extends ICrud<JuegoEntidad, JuegoForm, Long> {
    Optional<JuegoEntidad> obtenerPorTitulo(String titulo);
}
