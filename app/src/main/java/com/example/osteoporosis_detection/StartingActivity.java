package com.example.osteo_vision;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class StartingActivity extends AppCompatActivity {

    private ImageButton registrationIcon, predictionIcon, visualizationIcon, doctorsProfileIcon, homeIcon, settingsIcon, aboutIcon;

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

        setupIconClickAnimation(registrationIcon, TabularActivity.class);
        setupIconClickAnimation(predictionIcon, MainActivity.class);
        setupIconClickAnimation(visualizationIcon, Visualisation.class);
        setupIconClickAnimation(doctorsProfileIcon, Profile.class);

        setupIconHoverAnimation(homeIcon);
        setupIconHoverAnimation(settingsIcon);
        setupIconHoverAnimation(aboutIcon);
    }

    private void setupIconClickAnimation(ImageButton icon, final Class<?> activityToLaunch) {
        final Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.icon_click);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(clickAnimation);
                Intent intent = new Intent(StartingActivity.this, activityToLaunch);
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
}