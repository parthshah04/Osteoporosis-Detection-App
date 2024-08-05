package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.osteoporosis_detection.data.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    private ImageButton eyeButton;
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;
    private boolean isPasswordVisible = false; // Track password visibility
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        eyeButton = findViewById(R.id.eyeButton);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            String email = sharedPreferences.getString("email", "");
            navigateToStartingActivity(email);
        }

        // Add TextWatcher to email input for validation
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                if (!isValidEmail(email)) {
                    emailInput.setError(getString(R.string.invalid_email_format));
                } else {
                    emailInput.setError(null);
                }
            }
        });

        // Set click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (isValidEmail(email) && !password.isEmpty()) {
                    // Proceed with login
                    loginUser(email, password);
                } else {
                    if (!isValidEmail(email)) {
                        emailInput.setError(getString(R.string.invalid_email_format));
                    }
                    if (password.isEmpty()) {
                        passwordInput.setError(getString(R.string.password_cannot_be_empty));
                    }
                }
            }
        });

        // Set click listener for the eye button
        eyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eyeButton.setImageResource(R.drawable.eye_hide);
                } else {
                    // Show password
                    passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eyeButton.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;
                passwordInput.setSelection(passwordInput.getText().length()); // Move cursor to end
            }
        });
    }

    // Method to validate email format
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to handle user login
    private void loginUser(String email, String password) {
        Log.d(TAG, "Login attempt with email: " + email);
        Cursor cursor = dbHelper.getUser(email, password);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Login successful");
            // User exists, proceed with login
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("email", email);
            editor.apply();

            Toast.makeText(LoginActivity.this, R.string.login_successful, Toast.LENGTH_SHORT).show();
            // Navigate to StartingActivity after successful login
            navigateToStartingActivity(email);
        } else {
            // User does not exist or invalid credentials
            Log.d(TAG, "Invalid email or password");
            Toast.makeText(LoginActivity.this, R.string.invalid_email_or_password, Toast.LENGTH_SHORT).show();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void navigateToStartingActivity(String email) {
        Intent intent = new Intent(LoginActivity.this, StartingActivity.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        finish(); // Finish LoginActivity to prevent returning to it
    }

}
