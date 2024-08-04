package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageButton;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class StartingActivityTest {

    private StartingActivity activity;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(StartingActivity.class).create().resume().get();
        sharedPreferences = ApplicationProvider.getApplicationContext().getSharedPreferences("UserSession", 0);
    }

    @Test
    public void testActivityCreation() {
        assertNotNull(activity);
    }

    @Test
    public void testRegistrationIconClick() {
        ImageButton registrationIcon = activity.findViewById(R.id.registrationIcon);
        registrationIcon.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(MainActivity.class.getName(), startedIntent.getComponent().getClassName());
    }

    @Test
    public void testPredictionIconClick() {
        ImageButton predictionIcon = activity.findViewById(R.id.predictionIcon);
        predictionIcon.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(TabularActivity.class.getName(), startedIntent.getComponent().getClassName());
    }

    @Test
    public void testVisualizationIconClick() {
        ImageButton visualizationIcon = activity.findViewById(R.id.visualizationIcon);
        visualizationIcon.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(Visualisation.class.getName(), startedIntent.getComponent().getClassName());
    }

    @Test
    public void testDoctorsProfileIconClick() {
        ImageButton doctorsProfileIcon = activity.findViewById(R.id.doctorsProfileIcon);
        doctorsProfileIcon.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(ProfileActivity.class.getName(), startedIntent.getComponent().getClassName());
    }

    @Test
    public void testLogout() {
        // Set a dummy email in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", "test@example.com");
        editor.apply();

        // Perform logout
        activity.logout();

        // Check if SharedPreferences is cleared
        assertEquals("", sharedPreferences.getString("email", ""));

        // Check if LoginActivity is started
        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(LoginActivity.class.getName(), startedIntent.getComponent().getClassName());
    }
}