package org.sugarloader.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * @author Alexandre Gianquinto
 */

public class BaseNavActivity extends AppCompatActivity {

    private BottomNavigationView mNavigation;


    @Override
    public void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);

        if (mNavigation == null) {
            throw new IllegalStateException("View with R.id.navigation was not found ! Please #setContentView() in #onCreate()");
        }
        mNavigation.setOnNavigationItemSelectedListener(this::handleNav);
    }

    private boolean handleNav(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_1_activity_1_loader:
                startActivity(OneLoaderActivity.intent(this));
                return true;
            case R.id.nav_1_activity_2_loaders:
                startActivity(TwoLoadersActivity.intent(this));
            case R.id.nav_2_fragments_1_loader_each:
        }
        return false;
    }
}
