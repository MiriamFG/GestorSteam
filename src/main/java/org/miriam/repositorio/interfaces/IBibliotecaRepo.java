package org.miriam.repositorio.interfaces;

import org.miriam.modelo.entidad.BibliotecaEntidad;
import org.miriam.modelo.form.BibliotecaForm;

public interface IBibliotecaRepo extends ICrud<BibliotecaEntidad, BibliotecaForm, Long> {
    void eliminarJuegoUsuario(Long usuarioId, Long juegoId);
}
