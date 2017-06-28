package org.sugarloader.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.algi.sugarloader.SugarLoader;

/**
 * @author Alexandre Gianquinto
 */

public class OneLoaderActivity extends BaseNavActivity {


    public static final String EXTRA_SUCCESS = "EXTRA_SUCCESS";
    public static final String EXTRA_TIMEOUT = "EXTRA_TIMEOUT";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private View mProgress;
    private TextView mText;
    private SugarLoader<String> mLoader;

    public static Intent intent(Context context) {
        return new Intent(context, OneLoaderActivity.class);

    }

    public static Intent intent(Context context, boolean willSucceed, long howMuchTime, String message) {
        return intent(context)
                .putExtra(EXTRA_SUCCESS, willSucceed)
                .putExtra(EXTRA_TIMEOUT, howMuchTime)
                .putExtra(EXTRA_MESSAGE, message);

    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final boolean willSucceed = getIntent().getBooleanExtra(EXTRA_SUCCESS, true);
        final long timeout = getIntent().getLongExtra(EXTRA_TIMEOUT, 10000);
        final String message = getIntent().getStringExtra(EXTRA_MESSAGE);

        mProgress = findViewById(R.id.progress);
        mText = (TextView) findViewById(R.id.text);

        mLoader = new SugarLoader<String>("HelloLoader")
                .beforeStart(() -> {
                    mText.setVisibility(View.GONE);
                    mProgress.setVisibility(View.VISIBLE);
                })
                .beforeCreateLoader(() -> {
                    Toast.makeText(getBaseContext(), "Loader is created.", Toast.LENGTH_LONG).show();
                    ((Application) getApplicationContext()).counter.incrementAndGet();
                })
                .background(() -> {
                    Thread.sleep(timeout);
                    if (willSucceed) {
                        return message == null ? "SUCCES" : message;
                    } else {
                        throw new RuntimeException(message);
                    }
                })
                .beforeDeliver(() -> {
                    mText.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.GONE);
                })
                .onSuccess(this::onResult)
                .onError(this::onError);

        mText.setOnClickListener((view) -> mLoader.restart(this));
    }


    @Override
    protected void onResume() {
        super.onResume();

        mLoader.init(this);
    }

    private void onError(final Throwable error) {
        mText.setText("Erreur : " + error.getMessage());
    }

    private void onResult(final String result) {
        mText.setText(result);
    }
}
