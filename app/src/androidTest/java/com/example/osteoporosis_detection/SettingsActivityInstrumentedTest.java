package com.example.osteoporosis_detection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<SettingsActivity> activityRule =
            new ActivityScenarioRule<>(SettingsActivity.class);

    private Context context;
    private SharedPreferences sharedPreferences;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testThemeSwitching() {
        // Check initial state
        onView(withId(R.id.radio_light_mode)).check(matches(isDisplayed()));
        onView(withId(R.id.radio_dark_mode)).check(matches(isDisplayed()));

        // Switch to Dark Mode
        onView(withId(R.id.radio_dark_mode)).perform(click());
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, sharedPreferences.getInt("theme", -1));

        // Switch to Light Mode
        onView(withId(R.id.radio_light_mode)).perform(click());
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, sharedPreferences.getInt("theme", -1));
    }


    @Test
    public void testBackNavigation() {
        onView(withId(R.id.backIcon)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(StartingActivity.class.getName()));
    }
}
