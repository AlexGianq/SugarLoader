package org.sugarloader;

/**
 * @author Alexandre Gianquinto
 */
class Result<TYPE> {
    final TYPE success;
    final Throwable error;

    public Result(final TYPE success) {
        this.error = null;
        this.success = success;
    }

    public Result(final Throwable error) {
        this.success = null;
        this.error = error;
    }
}
