package com.example.osteoporosis_detection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 32) // Set to a supported SDK version
public class UpdateUserDetailsActivityTest {

    private Context context;
    private DatabaseHelper mockDbHelper;
    private SharedPreferences mockSharedPreferences;
    private SharedPreferences.Editor mockEditor;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        mockDbHelper = Mockito.mock(DatabaseHelper.class);
        mockSharedPreferences = Mockito.mock(SharedPreferences.class);
        mockEditor = Mockito.mock(SharedPreferences.Editor.class);

        Mockito.when(mockSharedPreferences.edit()).thenReturn(mockEditor);
    }

    @After
    public void tearDown() {
        // Ensure resources are closed
        if (mockDbHelper != null) {
            mockDbHelper.close();
        }
    }

    @Test
    public void testUpdateUserDetails() {
        Intent intent = new Intent(context, UpdateUserDetailsActivity.class);
        try (ActivityScenario<UpdateUserDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                // Verify the updateUserDetails method was called with correct parameters
                Mockito.verify(mockDbHelper).updateUserDetails(
                        "parth@gmail.com",
                        "Parth Shah",
                        "MD Physicians",
                        "9987654321",
                        "Admin@1234",
                        null
                );

                // Verify that the activity is finished after updating
                Mockito.verify(activity).finish();
            });
        }
    }
}
