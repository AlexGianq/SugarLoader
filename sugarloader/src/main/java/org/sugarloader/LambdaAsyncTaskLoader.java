package org.sugarloader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.sugarloader.function.Supplier;

/**
 * Loader that will take a supplier and store result on config change, so request will be played only
 * once.
 *
 * @author Alexandre Gianquinto
 */
class LambdaAsyncTaskLoader<T> extends AsyncTaskLoader<Result<T>> {
    private final Supplier<T> mBackgroundSupplier;

    LambdaAsyncTaskLoader(final Context context, final Supplier<T> backgroundSupplier) {
        super(context);
        mBackgroundSupplier = backgroundSupplier;
    }

    private Result<T> localResult;

    @Override
    public Result<T> loadInBackground() {
        try {
            return new Result<T>(mBackgroundSupplier.get());
        } catch (Exception e) {
            return new Result<T>(e);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (localResult == null) {
            forceLoad();
        } else {
            deliverResult(localResult);
        }
    }

    @Override
    public void deliverResult(final Result<T> data) {
        super.deliverResult(data);
        localResult = data;
    }
}
