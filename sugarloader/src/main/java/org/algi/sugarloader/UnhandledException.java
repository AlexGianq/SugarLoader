package org.algi.sugarloader;

/**
 * @author Alexandre Gianquinto
 */

class UnhandledException extends RuntimeException {
    public UnhandledException(final Throwable t) {
        super("Unhandled exception in lambda stream. You should try adding #onError(Throwable) handler on your LambdaLoader.", t);
    }
}
