package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.VideoView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLooper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class SplashActivityTest {

    private SplashActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(SplashActivity.class).create().get();
    }

    @Test
    public void testActivityCreation() {
        assertNotNull(activity);
    }

    @Test
    public void testVideoViewSetup() {
        VideoView videoView = activity.findViewById(R.id.videoView);
        assertNotNull(videoView);
        assertTrue(videoView.isPlaying());
    }

    @Test
    public void testNavigationWhenLoggedIn() {
        SharedPreferences.Editor editor = RuntimeEnvironment.application.getSharedPreferences("UserSession", 0).edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(StartingActivity.class.getName(), startedIntent.getComponent().getClassName());
    }

    @Test
    public void testNavigationWhenNotLoggedIn() {
        SharedPreferences.Editor editor = RuntimeEnvironment.application.getSharedPreferences("UserSession", 0).edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(LoginActivity.class.getName(), startedIntent.getComponent().getClassName());
    }
}