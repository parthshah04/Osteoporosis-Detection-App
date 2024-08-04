package com.example.osteoporosis_detection;

import android.content.Intent;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.test.core.app.ApplicationProvider;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowApplication;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(sdk = {32})
@RunWith(RobolectricTestRunner.class)
public class TabularActivityTest {

    private TabularActivity activity;
    private ActivityController<TabularActivity> controller;
    private DatabaseHelper mockDb;

    @Before
    public void setUp() {
        mockDb = mock(DatabaseHelper.class);
        controller = Robolectric.buildActivity(TabularActivity.class);
        activity = controller.get();

        // Initialize the lists
        activity.patientList = new ArrayList<>();
        activity.patientIds = new ArrayList<>();
        activity.originalPatientList = new ArrayList<>();
        activity.originalPatientIds = new ArrayList<>();
    }

    @Test
    public void testActivityCreation() {
        controller.create().start().resume();
        assertNotNull(activity);
    }

    @Test
    public void testBottomNavigationItemSelection() {
        controller.create().start().resume();

        activity.bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        Intent expectedIntent = new Intent(activity, StartingActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }
}