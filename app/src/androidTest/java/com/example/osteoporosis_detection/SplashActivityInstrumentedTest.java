package com.example.osteoporosis_detection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.VideoView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SplashActivityInstrumentedTest {

    private Context context;
    private SharedPreferences sharedPreferences;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void testVideoDisplayed() {
        ActivityScenario<SplashActivity> scenario = ActivityScenario.launch(SplashActivity.class);
        onView(withId(R.id.videoView)).check(matches(isDisplayed()));
        scenario.close();
    }

    @Test
    public void testNavigationToLoginWhenNotLoggedIn() throws Exception {
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        ActivityScenario.launch(SplashActivity.class);

        // Wait for the splash timeout
        Thread.sleep(5500);

        UiObject loginScreen = device.findObject(new UiSelector().packageName(context.getPackageName()).textContains("Login"));
        assertTrue("Login screen should be displayed", loginScreen.exists());
    }


}
