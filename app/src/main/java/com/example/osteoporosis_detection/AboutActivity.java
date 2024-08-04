package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AboutActivity extends AppCompatActivity {

    private ImageView backIcon, menuIcon;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeUIComponents();
        setupToolbar();
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        if (savedInstanceState == null) {
            // Load the Section1Fragment (About)
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            transaction1.replace(R.id.fragment_container_section1, new Section1Fragment());
            transaction1.commit();

            // Setup expandable sections
            setupExpandableSection(R.id.section3_heading, R.id.expandable_section3, R.id.fragment_container_section3, new Section3CardsFragment());
            setupExpandableSection(R.id.section2_heading, R.id.expandable_section2, R.id.fragment_container_section2, new Section2Fragment());
        }

        // Set up click listener for more info
        TextView textMoreInfo = findViewById(R.id.text_more_info);
        ImageView iconNext = findViewById(R.id.icon_next);

        View.OnClickListener moreInfoClickListener = v -> {
            Intent intent = new Intent(AboutActivity.this, InstructionActivity.class);
            startActivity(intent);
        };

        textMoreInfo.setOnClickListener(moreInfoClickListener);
        iconNext.setOnClickListener(moreInfoClickListener);
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
            Intent intent = new Intent(AboutActivity.this, StartingActivity.class);
            startActivity(intent);
            finish();
        });

        menuIcon.setOnClickListener(v -> showMenu());
    }

    private void showMenu() {
        PopupMenu popup = new PopupMenu(AboutActivity.this, menuIcon);
        popup.getMenuInflater().inflate(R.menu.header_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_settings) {
                startActivity(new Intent(AboutActivity.this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.menu_about) {
                // Already on About page, do nothing
                return true;
            } else if (itemId == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void setupExpandableSection(int headingId, int expandableId, int containerId, Fragment fragment) {
        TextView heading = findViewById(headingId);
        View expandableSection = findViewById(expandableId);

        heading.setOnClickListener(v -> {
            if (expandableSection.getVisibility() == View.GONE) {
                expandableSection.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(containerId, fragment);
                transaction.commit();
            } else {
                expandableSection.setVisibility(View.GONE);
            }
        });
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(AboutActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}