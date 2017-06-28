package org.sugarloader.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import org.algi.sugarloader.SugarLoader;

/**
 * 1 activity, 2 loaders. Loaders won't cross one another.
 *
 * @author Alexandre Gianquinto
 */
public class TwoLoadersActivity extends BaseNavActivity {

    public static Intent intent(final Context context) {
        return new Intent(context, TwoLoadersActivity.class);
    }

    private View mProgress1;
    private View mProgress2;

    private TextView mResult1;
    private TextView mResult2;
    private SugarLoader<String> mLoader1;

    private SugarLoader<String> mLoader2;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_2_loaders);


        mProgress1 = findViewById(R.id.progress1);
        mProgress2 = findViewById(R.id.progress2);

        mResult1 = (TextView) findViewById(R.id.result1);
        mResult2 = (TextView) findViewById(R.id.result2);

        mLoader1 = new SugarLoader<String>(1)
                .beforeStart(() -> {
                    mProgress1.setVisibility(View.VISIBLE);
                    mResult1.setVisibility(View.GONE);
                })
                .background(() -> {
                    Thread.sleep(4000);
                    return "Success 1";
                })
                .beforeDeliver(() -> {
                    mProgress1.setVisibility(View.GONE);
                    mResult1.setVisibility(View.VISIBLE);
                })
                .onSuccess((result) -> mResult1.setText(result));

        mLoader2 = new SugarLoader<String>(2)
                .beforeStart(() -> {
                    mProgress2.setVisibility(View.VISIBLE);
                    mResult2.setVisibility(View.GONE);
                })
                .background(() -> {
                    Thread.sleep(7000);
                    return "Success 2";
                })
                .beforeDeliver(() -> {
                    mProgress2.setVisibility(View.GONE);
                    mResult2.setVisibility(View.VISIBLE);
                })
                .onSuccess((result) -> mResult2.setText(result));


        mResult1.setOnClickListener((v) -> mLoader1.restart(this));
        mResult2.setOnClickListener((v) -> mLoader2.restart(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoader1.init(this);
        mLoader2.init(this);
    }
}
