//package com.example.osteoporosis_detection;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.not;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.espresso.intent.Intents;
//import androidx.test.espresso.intent.matcher.IntentMatchers;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.platform.app.InstrumentationRegistry;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class StartingActivityInstrumentedTest {
//
//    private SharedPreferences sharedPreferences;
//
//    @Before
//    public void setup() {
//        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("email", "test@example.com");
//        editor.putBoolean("isLoggedIn", true);
//        editor.apply();
//
//        Intents.init();
//    }
//
//    @After
//    public void tearDown() {
//        sharedPreferences.edit().clear().apply();
//        Intents.release();
//    }
//
//    @Test
//    public void testUIElementsDisplayed() {
//        ActivityScenario.launch(StartingActivity.class);
//
//        onView(withId(R.id.registrationIcon)).check(matches(isDisplayed()));
//        onView(withId(R.id.predictionIcon)).check(matches(isDisplayed()));
//        onView(withId(R.id.visualizationIcon)).check(matches(isDisplayed()));
//        onView(withId(R.id.doctorsProfileIcon)).check(matches(isDisplayed()));
//        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
//        onView(withId(R.id.menuIcon)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void testBottomNavigationNavigation() {
//        ActivityScenario.launch(StartingActivity.class);
//
//        onView(withId(R.id.navigation_registration)).perform(click());
//        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
//        Intents.release();
//        Intents.init();
//
//        onView(withId(R.id.navigation_prediction)).perform(click());
//        Intents.intended(IntentMatchers.hasComponent(TabularActivity.class.getName()));
//        Intents.release();
//        Intents.init();
//
//        onView(withId(R.id.navigation_visualization)).perform(click());
//        Intents.intended(IntentMatchers.hasComponent(Visualisation.class.getName()));
//        Intents.release();
//        Intents.init();
//
//        onView(withId(R.id.navigation_doctors_profile)).perform(click());
//        Intents.intended(IntentMatchers.hasComponent(ProfileActivity.class.getName()));
//    }
//
//    @Test
//    public void testMenuInteractions() {
//        ActivityScenario.launch(StartingActivity.class);
//
//        onView(withId(R.id.menuIcon)).perform(click());
//        onView(withText(R.string.settings)).check(matches(isDisplayed()));
//        onView(withText(R.string.about)).check(matches(isDisplayed()));
//        onView(withText(R.string.logout)).check(matches(isDisplayed()));
//
//        onView(withText(R.string.settings)).perform(click());
//        Intents.intended(IntentMatchers.hasComponent(SettingsActivity.class.getName()));
//        Intents.release();
//        Intents.init();
//
//        onView(withId(R.id.menuIcon)).perform(click());
//        onView(withText(R.string.about)).perform(click());
//        Intents.intended(IntentMatchers.hasComponent(AboutActivity.class.getName()));
//        Intents.release();
//        Intents.init();
//
//        onView(withId(R.id.menuIcon)).perform(click());
//        onView(withText(R.string.logout)).perform(click());
//        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
//    }
//
//    @Test
//    public void testLogout() {
//        ActivityScenario.launch(StartingActivity.class);
//
//        onView(withId(R.id.menuIcon)).perform(click());
//        onView(withText(R.string.logout)).perform(click());
//
//        // Verify that user session is cleared
//        assert(!sharedPreferences.getBoolean("isLoggedIn", false));
//        assert(sharedPreferences.getString("email", null) == null);
//
//        // Verify navigation to LoginActivity
//        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
//    }
//}

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