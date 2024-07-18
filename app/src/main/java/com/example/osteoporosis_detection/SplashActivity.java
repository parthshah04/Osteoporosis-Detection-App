package com.example.osteoporosis_detection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Button startDiagnosisButton;
    private Handler handler;
    private Runnable navigateToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize the button
        startDiagnosisButton = findViewById(R.id.startDiagnosisButton);

        // Set click listener for the button
        startDiagnosisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity immediately
                navigateToLogin();
            }
        });

        // Handler to navigate to LoginActivity after a delay
        handler = new Handler();
        navigateToLogin = new Runnable() {
            @Override
            public void run() {
                if (!SplashActivity.this.isFinishing()) {
                    navigateToLogin();
                }
            }
        };
        handler.postDelayed(navigateToLogin, 5000); // 5-second delay (adjust as needed)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending posts of navigateToLogin runnable
        handler.removeCallbacks(navigateToLogin);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish the SplashActivity to prevent returning to it
    }
}
