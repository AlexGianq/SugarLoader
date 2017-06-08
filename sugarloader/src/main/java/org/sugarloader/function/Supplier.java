package org.sugarloader.function;

/**
 * @author Alexandre Gianquinto
 */

public interface Supplier<T> {
    T get() throws Exception;
}
