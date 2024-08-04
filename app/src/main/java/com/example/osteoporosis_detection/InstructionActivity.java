package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

public class InstructionActivity extends AppCompatActivity {

    private ImageView backIcon, menuIcon;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        initializeUIComponents();
        setupToolbar();
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
    }

    private void initializeUIComponents() {
        backIcon = findViewById(R.id.backIcon);
        menuIcon = findViewById(R.id.menuIcon);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(InstructionActivity.this, AboutActivity.class);
            startActivity(intent);
            finish();
        });

        menuIcon.setOnClickListener(v -> showMenu());
    }

    private void showMenu() {
        PopupMenu popup = new PopupMenu(InstructionActivity.this, menuIcon);
        popup.getMenuInflater().inflate(R.menu.header_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_settings) {
                startActivity(new Intent(InstructionActivity.this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.menu_about) {
                startActivity(new Intent(InstructionActivity.this, AboutActivity.class));
                return true;
            } else if (itemId == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(InstructionActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}