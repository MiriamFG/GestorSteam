package transaction;

import java.util.Optional;
import java.util.function.Supplier;

import org.miriam.excepciones.FormularioInvalidoException;

/**
 * Abstracción de unidad de trabajo atómica.
 * Desacopla el manejo de transacciones de los repositorios y el controlador.
 */
public interface ITransactionManager {

    /**
     * Ejecuta {@code work} dentro de una unidad de trabajo atómica.
     * Si ocurre cualquier excepción, la unidad se deshace (rollback)
     * y retorna {@link Optional#empty()}.
     */
    <T> T inTransaction(ExceptionSupplier<T> work) throws FormularioInvalidoException;

}
