package org.lambdaloader.alex.sugarloader;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sugarloader.demo.Application;
import org.sugarloader.demo.OneLoaderActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SimpleInstrumentedTest {

    @Rule
    public final ActivityTestRule<OneLoaderActivity> activityRule = new ActivityTestRule<>(OneLoaderActivity.class, true, false);

    private Context mContext;

    @Before
    public void before() {
        mContext = InstrumentationRegistry.getContext();
        getApp().counter.set(0);
    }

    @Test
    public void should_loader_terminate_when_success() throws Exception {

        activityRule.launchActivity(OneLoaderActivity.intent(mContext, true, 5000, "Bravo !"));

        onView(withId(R.id.progress));
        onView(allOf(withId(R.id.progress), not(isDisplayed())));

        onView(allOf(withId(R.id.text), isDisplayed()))
                .check(matches(withText("Bravo !")));
        assertThat(getCounter(), is(1));
    }

    @Test
    public void should_loader_terminate_when_error() throws Exception {

        activityRule.launchActivity(OneLoaderActivity.intent(mContext, false, 5000, "C'est un échec !"));

        onView(withId(R.id.progress));
        onView(allOf(withId(R.id.progress), not(isDisplayed())));

        onView(allOf(withId(R.id.text), isDisplayed()))
                .check(matches(withText(containsString("C'est un échec !"))))
                .check((view, b) -> assertThat(getCounter(), is(1)));
    }

    @Test
    public void should_loader_load_once_however_rotations_may_happen() throws Exception {

        activityRule.launchActivity(OneLoaderActivity.intent(mContext, false, 5000, "C'est un échec !"));

        onView(withId(R.id.progress)).perform(rotate());

        onView(allOf(withId(R.id.progress), not(isDisplayed())));

        onView(allOf(withId(R.id.text), isDisplayed()))
                .check(matches(withText(containsString("C'est un échec !"))))
                .check((view, e) -> assertThat(getCounter(), is(1)));
    }

    @Test
    public void should_loader_load_twice_when_press_text() throws Exception {

        activityRule.launchActivity(OneLoaderActivity.intent(mContext, false, 5000, "C'est un échec !"));

        onView(withId(R.id.progress));
        onView(allOf(withId(R.id.progress), not(isDisplayed())));

        rotateScreen();

        onView(allOf(withId(R.id.text), isDisplayed()))
                .check(matches(withText(containsString("C'est un échec !"))))
                .perform(click());

        onView(withId(R.id.progress));
        onView(allOf(withId(R.id.text), isDisplayed()))
                .check((view, e) -> assertThat(getCounter(), is(2)));
    }

    /* ******************************************
     * Helpers
     * ******************************************/

    private Application getApp() {
        return (Application) InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    private void rotateScreen() {
        Context context = InstrumentationRegistry.getTargetContext();
        int orientation = context.getResources().getConfiguration().orientation;

        Activity activity = activityRule.getActivity();
        activity.setRequestedOrientation(
                (orientation == Configuration.ORIENTATION_PORTRAIT) ?
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @NonNull
    private ViewAction rotate() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return new BaseMatcher<View>() {
                    @Override
                    public boolean matches(final Object item) {
                        return true;
                    }

                    @Override
                    public void describeTo(final Description description) {
                    }
                };
            }

            @Override
            public String getDescription() {
                return "No desc";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                rotateScreen();
            }
        };
    }

    private int getCounter() {
        return getApp().counter.get();
    }

}
