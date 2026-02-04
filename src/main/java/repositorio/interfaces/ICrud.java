package repositorio.interfaces;

import java.util.Optional;

public interface ICrud <E, F, ID> {
    Optional<E> crear(F dto);
    /**
     * Obtiene una entidad por su identificador único.
     * @param id Identificador de la entidad.
     * @return Un Optional con la entidad encontrada, o vacío si no existe.
     */
    Optional<E> obtenerPorId(ID id);

    /**
     * Obtiene todas las entidades existentes.
     * @return Lista de todas las entidades.
     */
    List<E> obtenerTodos();

    /**
     * Actualiza una entidad existente a partir de su identificador y un DTO.
     * @param id Identificador de la entidad a actualizar.
     * @param dto Objeto de transferencia de datos con la información actualizada.
     * @return La entidad actualizada.
     */
    Optional<E> actualizar(ID id, F dto);

    /**
     * Elimina una entidad por su identificador único.
     * @param id Identificador de la entidad a eliminar.
     * @return true si la entidad fue eliminada, false en caso contrario.
     */
    boolean eliminar(ID id);
}
