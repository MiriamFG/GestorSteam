package repositorio.interfaces;

import modelo.entidad.JuegoEntidad;
import modelo.entidad.UsuarioEntidad;
import modelo.form.JuegoForm;
import modelo.form.UsuarioForm;

import java.util.Optional;

public interface IJuegoRepo extends ICrud<JuegoEntidad, JuegoForm, Long>{
    Optional<JuegoEntidad> obtenerPorTitulo(String titulo);
}
