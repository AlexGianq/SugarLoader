package org.sugarloader.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.algi.sugarloader.SugarLoader;

/**
 * Text with two fragments that share the same class, each one has same implementation, but will have different loaders.
 *
 * @author Alexandre Gianquinto
 */
public class TwoFragmentsActivity extends BaseNavActivity {
    public static Intent intent(final Context context) {
        return new Intent(context, TwoFragmentsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_2_fragments);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.placeholder1, TwoLoadersFragment.newInstance("Fragment1", 2000, 5000))
                    .replace(R.id.placeholder2, TwoLoadersFragment.newInstance("Fragment2", 3000, 4000))
                    .commit();
        }
    }

    public static class TwoLoadersFragment extends Fragment {

        public static final String ARG_FRAGMENT_NAME = "Fragment_name";
        public static final String ARG_TIMEOUT_1 = "timeout1";
        public static final String ARG_TIMEOUT_2 = "timeout2";
        private View mProgress1;
        private View mProgress2;
        private TextView mResult1;
        private TextView mResult2;
        private SugarLoader<String> mLoader1;
        private SugarLoader<String> mLoader2;

        public static TwoLoadersFragment newInstance(final String name, final int timeout1, final int timeout2) {
            final TwoLoadersFragment fragment = new TwoLoadersFragment();
            final Bundle args = new Bundle();
            args.putString(ARG_FRAGMENT_NAME, name);
            args.putInt(ARG_TIMEOUT_1, timeout1);
            args.putInt(ARG_TIMEOUT_2, timeout2);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_two_loaders, container, false);
        }

        @Override
        public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mProgress1 = view.findViewById(R.id.progress1);
            mProgress2 = view.findViewById(R.id.progress2);

            mResult1 = (TextView) view.findViewById(R.id.result1);
            mResult2 = (TextView) view.findViewById(R.id.result2);
        }

        @Override
        public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            final Bundle arguments = getArguments();
            final String fragmentName = arguments.getString(ARG_FRAGMENT_NAME);
            final int timeout1 = arguments.getInt(ARG_TIMEOUT_1);
            final int timeout2 = arguments.getInt(ARG_TIMEOUT_2);

            mLoader1 = new SugarLoader<String>("loader1")
                    .beforeStart(() -> {
                        mProgress1.setVisibility(View.VISIBLE);
                        mResult1.setVisibility(View.GONE);
                    })
                    .beforeDeliver(() -> {
                        mProgress1.setVisibility(View.GONE);
                        mResult1.setVisibility(View.VISIBLE);
                    })
                    .background(() -> {
                        Thread.sleep(timeout1);
                        return fragmentName + " : Success 1";
                    })
                    .onSuccess((result) -> mResult1.setText(result));

            mLoader2 = new SugarLoader<String>("loader2")
                    .beforeStart(() -> {
                        mProgress2.setVisibility(View.VISIBLE);
                        mResult2.setVisibility(View.GONE);
                    })
                    .beforeDeliver(() -> {
                        mProgress2.setVisibility(View.GONE);
                        mResult2.setVisibility(View.VISIBLE);
                    })
                    .background(() -> {
                        Thread.sleep(timeout2);
                        return fragmentName + " : Success 2";
                    })
                    .onSuccess((result) -> mResult2.setText(result));


            mResult1.setOnClickListener(v -> mLoader1.restart(this));
            mResult2.setOnClickListener(v -> mLoader2.restart(this));

            // This can be set in #onResume() too, but fragmentManager may raise a warning in logcat
            mLoader1.init(this);
            mLoader2.init(this);
        }

    }
}
