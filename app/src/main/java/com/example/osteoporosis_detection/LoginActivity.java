package com.example.osteoporosis_detection;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

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
                    emailInput.setError("Invalid email format");
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
                        emailInput.setError("Invalid email format");
                    }
                    if (password.isEmpty()) {
                        passwordInput.setError("Password cannot be empty");
                    }
                }
            }
        });
    }

    // Method to validate email format
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to handle user login
    private void loginUser(String email, String password) {
        // Here you can add your login logic, such as authentication with a server
        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
        // Navigate to StartingActivity after successful login
        Intent intent = new Intent(LoginActivity.this, StartingActivity.class);
        startActivity(intent);
        finish(); // Finish LoginActivity to prevent returning to it
    }
}