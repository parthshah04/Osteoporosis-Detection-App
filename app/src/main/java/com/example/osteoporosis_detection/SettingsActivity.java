package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private RadioButton radioLightMode;
    private RadioButton radioDarkMode;
    private SharedPreferences sharedPreferences;
    private TextView menuIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize views
        radioLightMode = findViewById(R.id.radio_light_mode);
        radioDarkMode = findViewById(R.id.radio_dark_mode);
        menuIcon = findViewById(R.id.menuIcon);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);

        // Set the radio button based on the saved theme
        int currentTheme = sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        updateRadioButtons(currentTheme);

        // Set up click listeners for radio buttons
        radioLightMode.setOnClickListener(v -> applyTheme(AppCompatDelegate.MODE_NIGHT_NO));
        radioDarkMode.setOnClickListener(v -> applyTheme(AppCompatDelegate.MODE_NIGHT_YES));

        // Set up the menu icon
        setupMenuIcon();
    }

    private void applyTheme(int themeMode) {
        // Save the selected theme
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("theme", themeMode);
        editor.apply();

        // Update radio buttons
        updateRadioButtons(themeMode);

        // Apply the theme
        AppCompatDelegate.setDefaultNightMode(themeMode);
        recreate();
    }

    private void updateRadioButtons(int themeMode) {
        radioLightMode.setChecked(themeMode == AppCompatDelegate.MODE_NIGHT_NO);
        radioDarkMode.setChecked(themeMode == AppCompatDelegate.MODE_NIGHT_YES);
    }

    private void setupMenuIcon() {
        menuIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(SettingsActivity.this, menuIcon);
            popup.getMenuInflater().inflate(R.menu.header_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_settings) {
                    // We're already on the Settings page, so do nothing
                    return true;
                } else if (itemId == R.id.menu_about) {
                    startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
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
        SharedPreferences userPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}