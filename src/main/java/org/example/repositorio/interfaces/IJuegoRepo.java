package org.example.repositorio.interfaces;

import org.example.modelo.entidad.JuegoEntidad;
import org.example.modelo.form.JuegoForm;

import java.util.Optional;

public interface IJuegoRepo extends ICrud<JuegoEntidad, JuegoForm, Long>{
    Optional<JuegoEntidad> obtenerPorTitulo(String titulo);
}
