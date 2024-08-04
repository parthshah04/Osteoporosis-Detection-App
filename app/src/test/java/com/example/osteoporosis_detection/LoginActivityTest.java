package com.example.osteoporosis_detection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.test.core.app.ApplicationProvider;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class LoginActivityTest {

    private LoginActivity activity;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private ImageButton eyeButton;
    private DatabaseHelper mockDbHelper;
    private SharedPreferences mockSharedPreferences;
    private SharedPreferences.Editor mockEditor;


    @Before
    public void setUp() {
        mockDbHelper = mock(DatabaseHelper.class);
        mockSharedPreferences = mock(SharedPreferences.class);
        mockEditor = mock(SharedPreferences.Editor.class);

        activity = Robolectric.buildActivity(LoginActivity.class).create().get();
        activity.dbHelper = mockDbHelper;
        activity.sharedPreferences = mockSharedPreferences;

        emailInput = activity.findViewById(R.id.emailInput);
        passwordInput = activity.findViewById(R.id.passwordInput);
        loginButton = activity.findViewById(R.id.loginButton);
        eyeButton = activity.findViewById(R.id.eyeButton);
    }

    @Test
    public void testLoginWithValidCredentials() {
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockDbHelper.getUser("test@example.com", "password123")).thenReturn(mockCursor);

        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        activity.emailInput.setText("test@example.com");
        activity.passwordInput.setText("password123");
        activity.loginButton.performClick();

        assertEquals("Login Successful", ShadowToast.getTextOfLatestToast());
        verify(mockEditor).putBoolean("isLoggedIn", true);
        verify(mockEditor).putString("email", "test@example.com");
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        when(mockDbHelper.getUser("invalid@example.com", "wrongpassword")).thenReturn(null);

        emailInput.setText("invalid@example.com");
        passwordInput.setText("wrongpassword");
        loginButton.performClick();

        assertEquals("Invalid Email or Password", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testInvalidEmailFormat() {
        emailInput.setText("invalidemail");
        loginButton.performClick();

        assertNotNull(emailInput.getError());
        assertEquals("Invalid Email Format", emailInput.getError().toString());
    }

    @Test
    public void testEmptyPassword() {
        emailInput.setText("valid@example.com");
        passwordInput.setText("");
        loginButton.performClick();

        assertNotNull(passwordInput.getError());
        assertEquals("Password Can't Be Empty", passwordInput.getError().toString());
    }

    @Test
    public void testPasswordVisibilityToggle() {
        eyeButton.performClick();
        assertFalse(passwordInput.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod);

        eyeButton.performClick();
        assertTrue(passwordInput.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod);
    }
    }
