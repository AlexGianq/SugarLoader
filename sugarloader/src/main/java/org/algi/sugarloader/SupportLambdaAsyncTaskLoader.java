package org.algi.sugarloader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.algi.sugarloader.function.Supplier;

/**
 * Support loader that will take a supplier and store result on config change, so request will be played only
 * once.
 *
 * @author Alexandre Gianquinto
 */
class SupportLambdaAsyncTaskLoader<T> extends AsyncTaskLoader<Result<T>> {

    private final Supplier<T> mBackgroundSupplier;

    SupportLambdaAsyncTaskLoader(final Context context, final Supplier<T> backgroundSupplier) {
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
