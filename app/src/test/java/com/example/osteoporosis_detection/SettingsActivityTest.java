package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

import com.example.osteoporosis_detection.LoginActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SettingsActivityTest {

    private SettingsActivity activity;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(SettingsActivity.class).create().get();
        sharedPreferences = activity.getSharedPreferences("ThemePrefs", SettingsActivity.MODE_PRIVATE);
    }

    @Test
    public void testInitialThemeSelection() {
        RadioButton radioLightMode = activity.findViewById(R.id.radio_light_mode);
        RadioButton radioDarkMode = activity.findViewById(R.id.radio_dark_mode);

        int currentTheme = sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        if (currentTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            assertTrue(radioLightMode.isChecked());
            assertFalse(radioDarkMode.isChecked());
        } else if (currentTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            assertFalse(radioLightMode.isChecked());
            assertTrue(radioDarkMode.isChecked());
        }
    }

    @Test
    public void testLightModeSelection() {
        RadioButton radioLightMode = activity.findViewById(R.id.radio_light_mode);
        radioLightMode.performClick();

        int savedTheme = sharedPreferences.getInt("theme", -1);
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, savedTheme);
    }

    @Test
    public void testDarkModeSelection() {
        RadioButton radioDarkMode = activity.findViewById(R.id.radio_dark_mode);
        radioDarkMode.performClick();

        int savedTheme = sharedPreferences.getInt("theme", -1);
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, savedTheme);
    }

    @Test
    public void testBackButtonNavigation() {
        activity.findViewById(R.id.backIcon).performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        Intent expectedIntent = new Intent(activity, StartingActivity.class);
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

}