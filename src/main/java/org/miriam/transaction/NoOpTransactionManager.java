package org.miriam.transaction;

import java.util.function.Supplier;

/**
 * Implementación no-op de {@link ITransactionManager}.
 * Se usa con repositorios en memoria donde no existe el concepto de transacción.
 */
public class NoOpTransactionManager implements ITransactionManager {

    @Override
    public <T> T inTransaction(Supplier<T> work) {
        return work.get();
    }
}
