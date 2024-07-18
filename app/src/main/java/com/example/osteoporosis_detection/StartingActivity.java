package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartingActivity extends AppCompatActivity {

    private ImageButton registrationIcon, predictionIcon, visualizationIcon, doctorsProfileIcon, homeIcon, settingsIcon, aboutIcon;
    private Button logoutButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        registrationIcon = findViewById(R.id.registrationIcon);
        predictionIcon = findViewById(R.id.predictionIcon);
        visualizationIcon = findViewById(R.id.visualizationIcon);
        doctorsProfileIcon = findViewById(R.id.doctorsProfileIcon);
        homeIcon = findViewById(R.id.homeIcon);
        settingsIcon = findViewById(R.id.settingsIcon);
        aboutIcon = findViewById(R.id.aboutIcon);
        logoutButton = findViewById(R.id.logoutButton);

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Get the logged-in user's email
        String userEmail = getIntent().getStringExtra("EMAIL");

        setupIconClickAnimation(registrationIcon, TabularActivity.class, userEmail);
        setupIconClickAnimation(predictionIcon, MainActivity.class, userEmail);
        setupIconClickAnimation(visualizationIcon, Visualisation.class, userEmail);
        setupIconClickAnimation(doctorsProfileIcon, ProfileActivity.class, userEmail);

        setupIconHoverAnimation(homeIcon);
        setupIconHoverAnimation(settingsIcon);
        setupIconHoverAnimation(aboutIcon);

        // Set click listener for the logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void setupIconClickAnimation(ImageButton icon, final Class<?> activityToLaunch, final String userEmail) {
        final Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.icon_click);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(clickAnimation);
                Intent intent = new Intent(StartingActivity.this, activityToLaunch);
                intent.putExtra("EMAIL", userEmail); // Pass the logged-in user's email to the next activity
                startActivity(intent);
            }
        });
    }

    private void setupIconHoverAnimation(final ImageButton icon) {
        final Animation hoverAnimation = AnimationUtils.loadAnimation(this, R.anim.icon_hover);
        final Animation hoverExitAnimation = AnimationUtils.loadAnimation(this, R.anim.icon_hover_exit);

        icon.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        v.startAnimation(hoverAnimation);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        v.startAnimation(hoverExitAnimation);
                        break;
                }
                return false;
            }
        });
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(StartingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish StartingActivity to prevent returning to it
    }
}
