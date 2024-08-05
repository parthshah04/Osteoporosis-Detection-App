package com.example.osteoporosis_detection;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.core.graphics.CanvasKt.withRotation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AboutActivityInstrumentedTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testUIComponentsAreDisplayed() {
        ActivityScenario.launch(AboutActivity.class);

        onView(withId(R.id.backIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.menuIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_container_section1)).check(matches(isDisplayed()));
        onView(withId(R.id.section2_header)).check(matches(isDisplayed()));
        onView(withId(R.id.section3_header)).check(matches(isDisplayed()));
        onView(withId(R.id.text_more_info)).check(matches(isDisplayed()));
        onView(withId(R.id.icon_next)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackIconNavigation() {
        ActivityScenario.launch(AboutActivity.class);

        onView(withId(R.id.backIcon)).perform(click());
        intended(hasComponent(StartingActivity.class.getName()));
    }

    @Test
    public void testMenuIconOpenPopup() {
        ActivityScenario.launch(AboutActivity.class);

        onView(withId(R.id.menuIcon)).perform(click());
        onView(withText(R.string.settings)).check(matches(isDisplayed()));
        onView(withText(R.string.about)).check(matches(isDisplayed()));
    }

    @Test
    public void testExpandableSection2() {
        ActivityScenario.launch(AboutActivity.class);

        onView(withId(R.id.expandable_section2)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.section2_header)).perform(click());
        onView(withId(R.id.expandable_section2)).check(matches(isDisplayed()));

        onView(withId(R.id.section2_header)).perform(click());
        onView(withId(R.id.expandable_section2)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void testExpandableSection3() {
        ActivityScenario.launch(AboutActivity.class);

        onView(withId(R.id.expandable_section3)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.section3_header)).perform(click());
        onView(withId(R.id.expandable_section3)).check(matches(isDisplayed()));

        onView(withId(R.id.section3_header)).perform(click());
        onView(withId(R.id.expandable_section3)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void testLogout() {
        ActivityScenario.launch(AboutActivity.class);

        onView(withId(R.id.menuIcon)).perform(click());
        onView(withText(R.string.logout)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

}
