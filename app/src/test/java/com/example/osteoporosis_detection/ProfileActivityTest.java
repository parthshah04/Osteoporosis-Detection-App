package com.example.osteoporosis_detection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class ProfileActivityTest {

    private ProfileActivity activity;
    private DatabaseHelper mockDbHelper;
    private SharedPreferences mockSharedPreferences;

    @Before
    public void setUp() {
        mockDbHelper = mock(DatabaseHelper.class);
        mockSharedPreferences = mock(SharedPreferences.class);

        when(mockSharedPreferences.getString("email", "")).thenReturn("test@example.com");

        activity = Robolectric.buildActivity(ProfileActivity.class).create().get();
    }

    @Test
    public void testUpdatePassword() {
        EditText passwordEditText = activity.findViewById(R.id.profilePassword);
        passwordEditText.setText("newPassword");

        activity.findViewById(R.id.updatePasswordButton).performClick();

        assertEquals("Password Updated", ShadowToast.getTextOfLatestToast());
    }

}