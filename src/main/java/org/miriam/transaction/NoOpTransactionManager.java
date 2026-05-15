package org.miriam.transaction;

import java.util.Optional;

import org.miriam.excepciones.FormularioInvalidoException;
import org.miriam.excepciones.FormularioInvalidoException;

/**
 * Implementaci�n no-op de {@link ITransactionManager}.
 * Se usa con repositorios en memoria donde no existe el concepto de
 * transacci�n.
 */
public class NoOpTransactionManager implements ITransactionManager {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T inTransaction(ExceptionSupplier<T> work) throws FormularioInvalidoException {
        try {
            return work.get();
        }catch(FormularioInvalidoException e){
            throw e;
        } 
        catch (Exception e) {
            try {
                return (T) Optional.empty();
            } catch (ClassCastException ex) {
                return null;
            }
        }
    }
}
