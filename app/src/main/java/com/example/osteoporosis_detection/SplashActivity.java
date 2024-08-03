package com.example.osteoporosis_detection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable navigateToLogin;
    private ViewPager2 viewPager;
    private Button skipButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        if (isUserLoggedIn()) {
            // If logged in, navigate directly to StartingActivity
            navigateToStarting();
            return; // Return here to prevent further execution of the splash screen setup
        }

        // If not logged in, proceed with splash screen
        setContentView(R.layout.activity_splash);

        // Initialize ViewPager and adapter
        viewPager = findViewById(R.id.viewPager);
        int[] layouts = new int[]{
                R.layout.screen_one,
                R.layout.screen_two,
                R.layout.screen_three
        };
        SplashPagerAdapter adapter = new SplashPagerAdapter(layouts);
        viewPager.setAdapter(adapter);

        // Initialize the skip button
        skipButton = findViewById(R.id.skipButton);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });


        // Handler to navigate to LoginActivity after a delay
        handler = new Handler();
        navigateToLogin = new Runnable() {
            @Override
            public void run() {
                if (!SplashActivity.this.isFinishing()) {
                    navigateToLogin();
                }
            }
        };
        handler.postDelayed(navigateToLogin, 10000); // 10-second delay (adjust as needed)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending posts of navigateToLogin runnable
        if (handler != null) {
            handler.removeCallbacks(navigateToLogin);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish the SplashActivity to prevent returning to it
    }

    private void navigateToStarting() {
        Intent intent = new Intent(SplashActivity.this, StartingActivity.class);
        startActivity(intent);
        finish(); // Finish the SplashActivity to prevent returning to it
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
