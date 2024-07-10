package com.example.osteoporosis_detection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Button startDiagnosisButton;

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
                // Start the LoginActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish the SplashActivity to prevent returning to it
            }
        });

        // Optional: Auto-navigate to LoginActivity after a delay (e.g., if no user interaction with the button)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!SplashActivity.this.isFinishing()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 5000); // 5-second delay (adjust as needed)
    }
}