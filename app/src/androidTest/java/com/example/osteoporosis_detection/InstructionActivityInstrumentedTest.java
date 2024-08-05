package com.example.osteoporosis_detection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class InstructionActivityInstrumentedTest {

    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testToolbarIsDisplayed() {
        ActivityScenario.launch(InstructionActivity.class);
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.backIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.menuIcon)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackIconNavigation() {
        ActivityScenario.launch(InstructionActivity.class);
        onView(withId(R.id.backIcon)).perform(click());
        intended(hasComponent(AboutActivity.class.getName()));
    }

    @Test
    public void testMenuDisplay() {
        ActivityScenario.launch(InstructionActivity.class);
        onView(withId(R.id.menuIcon)).perform(click());
        onView(withText(R.string.settings)).check(matches(isDisplayed()));
        onView(withText(R.string.about)).check(matches(isDisplayed()));
        onView(withText(R.string.logout)).check(matches(isDisplayed()));
    }

    @Test
    public void testSettingsNavigation() {
        ActivityScenario.launch(InstructionActivity.class);
        onView(withId(R.id.menuIcon)).perform(click());
        onView(withText(R.string.settings)).perform(click());
        intended(hasComponent(SettingsActivity.class.getName()));
    }

    @Test
    public void testAboutNavigation() {
        ActivityScenario.launch(InstructionActivity.class);
        onView(withId(R.id.menuIcon)).perform(click());
        onView(withText(R.string.about)).perform(click());
        intended(hasComponent(AboutActivity.class.getName()));
    }

}
