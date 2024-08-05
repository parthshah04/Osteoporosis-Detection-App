package com.example.osteoporosis_detection;

import static org.mockito.Mockito.*;

import android.os.Bundle;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class DeleteUserActivityTest {

    private TestDeleteUserActivity activity;
    private static DatabaseHelper mockDatabaseHelper;

    @Before
    public void setUp() {
        mockDatabaseHelper = mock(DatabaseHelper.class);
        activity = Robolectric.buildActivity(TestDeleteUserActivity.class).create().get();
    }

    @Test
    public void testActivityFinishesAfterDeletion() {
        assert(activity.isFinishing());
    }

    // This is a concrete implementation of the abstract class for testing purposes
    public static class TestDeleteUserActivity extends DeleteUserActivity {
        @Override
        protected DatabaseHelper createDatabaseHelper() {
            return mockDatabaseHelper;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}