package com.example.osteoporosis_detection;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
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
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StartingActivityInstrumentedTest {

    private Context context;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Initialize Intents for intent verification
        Intents.init();

        // Set up a mock email in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", "test@example.com");
        editor.apply();
    }

    @After
    public void tearDown() {
        // Clean up Intents
        Intents.release();

        // Clear SharedPreferences after each test
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Test
    public void testActivityLaunch() {
        ActivityScenario<StartingActivity> scenario = ActivityScenario.launch(StartingActivity.class);
        onView(withId(R.id.registrationIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.predictionIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.visualizationIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.doctorsProfileIcon)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegistrationIconClick() {
        ActivityScenario<StartingActivity> scenario = ActivityScenario.launch(StartingActivity.class);
        onView(withId(R.id.registrationIcon)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testPredictionIconClick() {
        ActivityScenario<StartingActivity> scenario = ActivityScenario.launch(StartingActivity.class);
        onView(withId(R.id.predictionIcon)).perform(click());
        intended(hasComponent(TabularActivity.class.getName()));
    }

    @Test
    public void testVisualizationIconClick() {
        ActivityScenario<StartingActivity> scenario = ActivityScenario.launch(StartingActivity.class);
        onView(withId(R.id.visualizationIcon)).perform(click());
        intended(hasComponent(Visualisation.class.getName()));
    }

    @Test
    public void testDoctorsProfileIconClick() {
        ActivityScenario<StartingActivity> scenario = ActivityScenario.launch(StartingActivity.class);
        onView(withId(R.id.doctorsProfileIcon)).perform(click());
        intended(hasComponent(ProfileActivity.class.getName()));
    }

    @Test
    public void testLogout() {
        ActivityScenario<StartingActivity> scenario = ActivityScenario.launch(StartingActivity.class);
        scenario.onActivity(activity -> {
            activity.logout();
            String email = sharedPreferences.getString("email", null);
            assertEquals(null, email);
        });
        intended(hasComponent(LoginActivity.class.getName()));
    }
}