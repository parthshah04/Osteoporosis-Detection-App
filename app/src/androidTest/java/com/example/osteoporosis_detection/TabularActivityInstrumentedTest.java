package com.example.osteoporosis_detection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TabularActivityInstrumentedTest {

    private SharedPreferences sharedPreferences;
    private DatabaseHelper db;

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", "test@example.com");
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        db = new DatabaseHelper(context);
        // Add some test data
        db.insertPredictionData("John Doe", "john@example.com", "30", "0.7", "0.8", "Positive",
                1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, "path/to/xray1.jpg", 0.75f, true);
        db.insertPredictionData("Jane Smith", "jane@example.com", "45", "0.6", "0.5", "Negative",
                0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, "path/to/xray2.jpg", 0.55f, true);

        Intents.init();
    }

    @After
    public void tearDown() {
        sharedPreferences.edit().clear().apply();
        // Delete all patients
        Cursor cursor = db.getAllPatients();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                db.deletePatient(id);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        Intents.release();
    }

    @Test
    public void testUIElementsDisplayed() {
        ActivityScenario.launch(TabularActivity.class);

        onView(withId(R.id.listViewPatients)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonBack)).check(matches(isDisplayed()));
        onView(withId(R.id.searchView)).check(matches(isDisplayed()));
        onView(withId(R.id.backIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.menuIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
    }

    @Test
    public void testPatientDataLoaded() {
        ActivityScenario.launch(TabularActivity.class);

        onView(withText("John Doe - john@example.com")).check(matches(isDisplayed()));
        onView(withText("Jane Smith - jane@example.com")).check(matches(isDisplayed()));
    }

    @Test
    public void testLogout() {
        ActivityScenario.launch(TabularActivity.class);

        onView(withId(R.id.menuIcon)).perform(click());
        onView(withText(R.string.logout)).perform(click());

        // Verify that user session is cleared
        assert(!sharedPreferences.getBoolean("isLoggedIn", false));
        assert(sharedPreferences.getString("email", null) == null);

        // Verify navigation to LoginActivity
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
    }
}
