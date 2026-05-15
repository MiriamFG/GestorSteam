package org.miriam.transaction;

import org.miriam.excepciones.FormularioInvalidoException;

public interface ExceptionSupplier<T> {

    T get() throws FormularioInvalidoException;
}
