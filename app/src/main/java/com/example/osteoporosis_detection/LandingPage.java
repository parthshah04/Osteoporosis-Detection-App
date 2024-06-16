package com.example.osteoporosis_detection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LandingPage extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.newCase), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button newCase = findViewById(R.id.newCase);
        Button patients = findViewById(R.id.patients);
        Button dashboard = findViewById(R.id.dashboard);
        Button profile = findViewById(R.id.profile);
        Button logout = findViewById(R.id.logout);


        newCase.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPage.this, newCaseActivity.class);
            startActivity(intent);
        });

        patients.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPage.this, PatientActivity.class);
            startActivity(intent);
        });

        dashboard.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPage.this, DashboardActivity.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPage.this, ProfileActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPage.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(LandingPage.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        });
    }
}