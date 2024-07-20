package com.example.osteoporosis_detection;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private RadioButton radioLightMode;
    private RadioButton radioDarkMode;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        radioLightMode = findViewById(R.id.radio_light_mode);
        radioDarkMode = findViewById(R.id.radio_dark_mode);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);

        // Set the radio button based on the saved theme
        int currentTheme = sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        updateRadioButtons(currentTheme);

        // Set up click listeners for radio buttons
        radioLightMode.setOnClickListener(v -> applyTheme(AppCompatDelegate.MODE_NIGHT_NO));
        radioDarkMode.setOnClickListener(v -> applyTheme(AppCompatDelegate.MODE_NIGHT_YES));
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
}