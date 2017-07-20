package org.algi.sugarloader;

/**
 * @author Alexandre Gianquinto
 */
class Result<TYPE> {
    final TYPE result;
    final Throwable error;
    final boolean isSuccess;

    Result(final TYPE result) {
        this.error = null;
        this.result = result;
        this.isSuccess = true;
    }

    Result(final Throwable error) {
        this.result = null;
        this.error = error;
        this.isSuccess = false;
    }
}
