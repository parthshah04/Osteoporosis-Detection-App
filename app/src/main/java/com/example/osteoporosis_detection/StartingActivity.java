package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StartingActivity extends AppCompatActivity {

    private ImageButton registrationIcon, predictionIcon, visualizationIcon, doctorsProfileIcon;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private TextView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        registrationIcon = findViewById(R.id.registrationIcon);
        predictionIcon = findViewById(R.id.predictionIcon);
        visualizationIcon = findViewById(R.id.visualizationIcon);
        doctorsProfileIcon = findViewById(R.id.doctorsProfileIcon);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        menuIcon = findViewById(R.id.menuIcon);

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");

        setupIconClickAnimation(registrationIcon, MainActivity.class, userEmail);
        setupIconClickAnimation(predictionIcon, TabularActivity.class, userEmail);
        setupIconClickAnimation(visualizationIcon, Visualisation.class, userEmail);
        setupIconClickAnimation(doctorsProfileIcon, ProfileActivity.class, userEmail);

        setupBottomNavigation(userEmail);
        setupMenuIcon();
    }

    private void setupIconClickAnimation(ImageButton icon, final Class<?> activityToLaunch, final String userEmail) {
        final Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.icon_click);
        icon.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            Intent intent = new Intent(StartingActivity.this, activityToLaunch);
            intent.putExtra("EMAIL", userEmail);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation(String userEmail) {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                // Stay on current activity
                return true;
            } else if (itemId == R.id.navigation_registration) {
                intent = new Intent(StartingActivity.this, MainActivity.class);
            } else if (itemId == R.id.navigation_prediction) {
                intent = new Intent(StartingActivity.this, TabularActivity.class);
            } else if (itemId == R.id.navigation_visualization) {
                intent = new Intent(StartingActivity.this, Visualisation.class);
            } else if (itemId == R.id.navigation_doctors_profile) {
                intent = new Intent(StartingActivity.this, ProfileActivity.class);
            } else {
                return false;
            }

            intent.putExtra("EMAIL", userEmail);
            startActivity(intent);
            return true;
        });
    }

    private void setupMenuIcon() {
        menuIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(StartingActivity.this, menuIcon);
            popup.getMenuInflater().inflate(R.menu.header_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_settings) {
                    startActivity(new Intent(StartingActivity.this, SettingsActivity.class));
                    return true;
                } else if (itemId == R.id.menu_about) {
                    startActivity(new Intent(StartingActivity.this, AboutActivity.class));
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    logout();
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(StartingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}