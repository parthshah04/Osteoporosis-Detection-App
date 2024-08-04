package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.test.core.app.ApplicationProvider;

import com.example.osteoporosis_detection.InstructionActivity;
import com.example.osteoporosis_detection.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPopupMenu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class InstructionActivityTest {

    private InstructionActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(InstructionActivity.class).create().resume().get();
    }

    @Test
    public void testActivityCreation() {
        assertNotNull(activity);
    }

    @Test
    public void testUIComponentsInitialization() {
        ImageView backIcon = activity.findViewById(R.id.backIcon);
        ImageView menuIcon = activity.findViewById(R.id.menuIcon);

        assertNotNull(backIcon);
        assertNotNull(menuIcon);
    }

    @Test
    public void testToolbarSetup() {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);

        ActionBar actionBar = activity.getSupportActionBar();
        assertNotNull(actionBar);
    }

    @Test
    public void testBackIconClick() {
        ImageView backIcon = activity.findViewById(R.id.backIcon);
        backIcon.performClick();

        Intent expectedIntent = new Intent(activity, AboutActivity.class);
        Intent actualIntent = shadowOf(activity).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());

        assertTrue(activity.isFinishing());
    }

}