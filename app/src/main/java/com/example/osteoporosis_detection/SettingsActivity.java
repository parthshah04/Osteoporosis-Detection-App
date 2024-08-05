package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
    private ImageView backIcon, menuIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeUIComponents();
        setupToolbar();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);

        // Set the radio button based on the saved theme
        int currentTheme = sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        updateRadioButtons(currentTheme);

        // Set up click listeners for radio buttons
        radioLightMode.setOnClickListener(v -> applyTheme(AppCompatDelegate.MODE_NIGHT_NO));
        radioDarkMode.setOnClickListener(v -> applyTheme(AppCompatDelegate.MODE_NIGHT_YES));
    }

    private void initializeUIComponents() {
        radioLightMode = findViewById(R.id.radio_light_mode);
        radioDarkMode = findViewById(R.id.radio_dark_mode);
        backIcon = findViewById(R.id.backIcon);
        menuIcon = findViewById(R.id.menuIcon);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(R.string.settings);

        backIcon.setOnClickListener(v -> onBackPressed());

        menuIcon.setOnClickListener(v -> showMenu());
    }

    private void showMenu() {
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
    }

    private void applyTheme(int themeMode) {
        Log.d("SettingsActivity", "Applying theme: " + (themeMode == AppCompatDelegate.MODE_NIGHT_YES ? "Dark" : "Light"));

        // Save the selected theme
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("theme", themeMode);
        editor.apply();

        // Update radio buttons
        updateRadioButtons(themeMode);

        // Apply the theme
        AppCompatDelegate.setDefaultNightMode(themeMode);

        // Recreate the activity to apply the theme
        recreate();
    }

    private void updateRadioButtons(int themeMode) {
        radioLightMode.setChecked(themeMode == AppCompatDelegate.MODE_NIGHT_NO);
        radioDarkMode.setChecked(themeMode == AppCompatDelegate.MODE_NIGHT_YES);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, StartingActivity.class);
        startActivity(intent);
        finish();
    }
}