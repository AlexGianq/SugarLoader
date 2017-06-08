package org.algi.sugarloader;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;

import org.algi.sugarloader.function.Call;
import org.algi.sugarloader.function.Consumer;
import org.algi.sugarloader.function.Supplier;

/**
 * <p>LambdaLoader is a builder that provides a syntaxic sugar to play with google's loader API.</p>
 * For example, the following code :
 * <pre>{code
 *      new LambdaLoader()
 *          .background(() -> myService.fetchData())
 *          .onSuccess(data -> mTextView.setText(data.label))
 *          .init(this) // "this" is activity, compat activity, fragment or compat fragment
 * }
 * </pre>
 * ... is equivalent to :
 * <pre>{@code
 *
 *      @Override
 *      public void onCreate(SavedInstanceState bundle) {
 *          super.onCreate(bundle);
 *          getLoaderManager().initLoader(LOADER_id, null, this);
 *      }
 *
 *      ...
 *
 *      @Override
 *      public Loader<T> onCreateLoader(final int i, final Bundle bundle) {
 *          return new MyPersonalLoader(context, params...);
 *      }
 *
 *      @Override
 *      public void onLoadFinished(final Loader<T> loader, final T data) {
 *          mTextView.setText(data.label);
 *      }
 *
 *
 *      ...
 *
 *      public class MyPersonalLoader extends AsyncTaskLoader<T> {
 *
 *          ... // Constructor, variables, etc...
 *
 *          @Override
 *          public T loadInBackground() {
 *              return mService.fetchData();
 *          }
 *
 *          ...
 *      }
 * }
 * </pre>
 * ... and also provides error resilience and some handlers that interfere in loader lifecycle.
 * <p>As in Loader API, be careful where you invoke {@link #init} method : it may execute
 * synchronously (immediate) or asynchronously, depending on whether the result is already available
 * or has to be fetched. So be careful that the variables you are using should be instanced first !
 * </p>
 * <p>
 * Caution, if you must use multiple loaders inside a same fragment/activity, then use the named
 * constructor (@{link {@link #SugarLoader(String)}} or {@link #SugarLoader(int)}}), that will
 * help Android manage their lifecycle separately. If you have only one loader on a single page,
 * then a default ID will be used.
 * </p>
 *
 * @author Alexandre Gianquinto
 */

public class SugarLoader<T> {

    private static final int DEFAULT_ID = 795462135;

    private final int id;

    public SugarLoader() {
        id = DEFAULT_ID;
    }

    public SugarLoader(final String name) {
        this.id = name.hashCode();
    }

    public SugarLoader(final int id) {
        this.id = id;
    }

    @NonNull
    private Supplier<T> mBackgroundSupplier = Nothing.doNothing();

    @NonNull
    private Consumer<T> mSuccessConsumer = Nothing.doNothing();

    @NonNull
    private Consumer<Throwable> mErrorConsumer = new Consumer<Throwable>() {
        @Override
        public void accept(final Throwable t) {
            throw new UnhandledException(t);
        }
    };

    @NonNull
    private Call mBefore = Nothing.doNothing();

    @NonNull
    private Call mBeforeDeliver = Nothing.doNothing();

    @NonNull
    private Call mBeforeCreate = Nothing.doNothing();

    /**
     * Provide an operation to be executed on background thread.
     *
     * @param backgroundSupplier the operation to be executed. e.g. {@code () -> mService.fetchData(p1, p2)}
     * @return the builder itself, to pipe with other builder commands
     */
    public SugarLoader<T> background(@NonNull Supplier<T> backgroundSupplier) {
        mBackgroundSupplier = backgroundSupplier;
        return this;
    }

    @UiThread
    public SugarLoader<T> onSuccess(@NonNull Consumer<T> uiThreadConsumer) {
        mSuccessConsumer = uiThreadConsumer;
        return this;
    }

    @UiThread
    public SugarLoader<T> onError(@NonNull Consumer<Throwable> errorConsumer) {
        mErrorConsumer = errorConsumer;
        return this;
    }


    /**
     * This handler will be called before loader is started.
     */
    public SugarLoader<T> beforeStart(@NonNull Call before) {
        mBefore = before;
        return this;
    }

    /**
     * This handler will be called when loader object is effectively created. This won't happen when
     * fragment simply bounds to existing running loader.
     */
    public SugarLoader<T> beforeCreateLoader(@NonNull Call beforeCreate) {
        mBeforeCreate = beforeCreate;
        return this;
    }

    /**
     * This handler will be called before result is delivered, on UI Thread.
     * This may be useful to do actions that are independant of the result (hiding a ProgressBar,
     * showing a text, ...)
     */
    public SugarLoader<T> beforeDeliver(@NonNull Call before) {
        mBeforeDeliver = before;
        return this;
    }

    /* ******************************************
     * Support loaders
     * ******************************************/

    public void init(final AppCompatActivity activity) {
        mBefore.apply();
        activity.getSupportLoaderManager().initLoader(id, null, getSupportLoaderCallbacks(activity));
    }

    public void init(final android.support.v4.app.Fragment fragment) {
        mBefore.apply();
        fragment.getLoaderManager().initLoader(id, null, getSupportLoaderCallbacks(fragment.getContext()));
    }

    public void restart(final AppCompatActivity activity) {
        mBefore.apply();
        activity.getSupportLoaderManager().restartLoader(id, null, getSupportLoaderCallbacks(activity.getBaseContext()));
    }

    public void restart(final android.support.v4.app.Fragment fragment) {
        mBefore.apply();
        fragment.getLoaderManager().restartLoader(id, null, getSupportLoaderCallbacks(fragment.getContext()));
    }

    /* ******************************************
     * Regular loaders
     * ******************************************/


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void init(final Activity activity) {
        mBefore.apply();
        activity.getLoaderManager().initLoader(id, null, getLoaderCallbacks(activity));
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void init(final Fragment fragment) {
        mBefore.apply();
        fragment.getLoaderManager().initLoader(id, null, getLoaderCallbacks(fragment.getActivity()));
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void restart(final Activity activity) {
        mBefore.apply();
        activity.getLoaderManager().restartLoader(id, null, new android.app.LoaderManager.LoaderCallbacks<Result<T>>() {
            @Override
            public android.content.Loader<Result<T>> onCreateLoader(final int i, final Bundle bundle) {
                mBeforeCreate.apply();
                return new LambdaAsyncTaskLoader<>(activity.getBaseContext(), mBackgroundSupplier);
            }

            @Override
            public void onLoadFinished(final android.content.Loader<Result<T>> loader, final Result<T> tResult) {
                mBeforeDeliver.apply();
                if (tResult.success != null) {
                    mSuccessConsumer.accept(tResult.success);
                } else {
                    mErrorConsumer.accept(tResult.error);
                }
            }

            @Override
            public void onLoaderReset(final android.content.Loader<Result<T>> loader) {
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void restart(final Fragment fragment) {
        mBefore.apply();
        fragment.getLoaderManager().restartLoader(id, null, getLoaderCallbacks(fragment.getActivity()));
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void destroy(Fragment fragment) {
        fragment.getLoaderManager().destroyLoader(id);
    }

    /* ******************************************
     * Loader callbacks (duplicated for supoprt)
     * ******************************************/

    @NonNull
    private android.support.v4.app.LoaderManager.LoaderCallbacks<Result<T>> getSupportLoaderCallbacks(final Context baseContext) {
        return new android.support.v4.app.LoaderManager.LoaderCallbacks<Result<T>>() {
            @Override
            public android.support.v4.content.Loader<Result<T>> onCreateLoader(final int i, final Bundle bundle) {
                mBeforeCreate.apply();
                return new SupportLambdaAsyncTaskLoader<>(baseContext, mBackgroundSupplier);
            }

            @Override
            public void onLoadFinished(final android.support.v4.content.Loader<Result<T>> loader, final Result<T> tResult) {
                mBeforeDeliver.apply();
                if (tResult.success != null) {
                    mSuccessConsumer.accept(tResult.success);
                } else {
                    mErrorConsumer.accept(tResult.error);
                }
            }

            @Override
            public void onLoaderReset(final android.support.v4.content.Loader<Result<T>> loader) {
            }
        };
    }

    @NonNull
    private LoaderManager.LoaderCallbacks<Result<T>> getLoaderCallbacks(final Context baseContext) {
        return new LoaderManager.LoaderCallbacks<Result<T>>() {
            @Override
            public Loader<Result<T>> onCreateLoader(final int i, final Bundle bundle) {
                mBeforeCreate.apply();
                return new LambdaAsyncTaskLoader<>(baseContext, mBackgroundSupplier);
            }

            @Override
            public void onLoadFinished(final Loader<Result<T>> loader, final Result<T> tResult) {
                mBeforeDeliver.apply();
                if (tResult.success != null) {
                    mSuccessConsumer.accept(tResult.success);
                } else {
                    mErrorConsumer.accept(tResult.error);
                }
            }

            @Override
            public void onLoaderReset(final Loader<Result<T>> loader) {
            }
        };
    }

}
