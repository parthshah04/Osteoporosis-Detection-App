package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.example.osteoporosis_detection.data.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private ImageView profileImage, menuIcon, backIcon;
    private EditText profileName, profileDesignation, profilePhone, profileEmail, profilePassword;
    private Button changeProfileImageButton, updatePasswordButton;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private String email;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        profileDesignation = findViewById(R.id.profileDesignation);
        profilePhone = findViewById(R.id.profilePhone);
        profileEmail = findViewById(R.id.profileEmail);
        profilePassword = findViewById(R.id.profilePassword);
        changeProfileImageButton = findViewById(R.id.changeProfileImageButton);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        menuIcon = findViewById(R.id.menuIcon);
        backIcon = findViewById(R.id.backIcon);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Get logged in user's email from SharedPreferences
        email = sharedPreferences.getString("email", "");

        if (!email.isEmpty()) {
            // Load user data
            loadUserData(email);
        } else {
            Toast.makeText(this, R.string.error_no_email_provided, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set click listener for changing profile image
        changeProfileImageButton.setOnClickListener(v -> openImagePicker());

        // Set click listener for updating password
        updatePasswordButton.setOnClickListener(v -> updatePassword());

        // Set click listener for back icon
        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, StartingActivity.class);
            startActivity(intent);
            finish();
        });

        setupBottomNavigation();
        setupMenuIcon();
    }

    private void loadUserData(String email) {
        Cursor cursor = dbHelper.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            int designationIndex = cursor.getColumnIndex("designation");
            int phoneIndex = cursor.getColumnIndex("phone");
            int emailIndex = cursor.getColumnIndex("email");
            int profilePhotoIndex = cursor.getColumnIndex("profile_photo");

            if (nameIndex != -1) {
                profileName.setText(cursor.getString(nameIndex));
            }
            if (designationIndex != -1) {
                profileDesignation.setText(cursor.getString(designationIndex));
            }
            if (phoneIndex != -1) {
                profilePhone.setText(cursor.getString(phoneIndex));
            }
            if (emailIndex != -1) {
                profileEmail.setText(cursor.getString(emailIndex));
            }
            if (profilePhotoIndex != -1) {
                byte[] imageBytes = cursor.getBlob(profilePhotoIndex);
                if (imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profileImage.setImageBitmap(bitmap);
                }
            }
        } else {
            Toast.makeText(this, R.string.error_user_not_found, Toast.LENGTH_SHORT).show();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                profileImage.setImageBitmap(bitmap);
                saveProfileImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveProfileImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        dbHelper.updateUserProfileImage(email, imageBytes);
        Toast.makeText(ProfileActivity.this, R.string.profile_image_updated, Toast.LENGTH_SHORT).show();
    }

    private void updatePassword() {
        String newPassword = profilePassword.getText().toString().trim();
        if (!newPassword.isEmpty()) {
            dbHelper.updateUserPassword(email, newPassword);
            Toast.makeText(ProfileActivity.this, R.string.password_updated, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ProfileActivity.this, R.string.password_cannot_be_empty, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(ProfileActivity.this, StartingActivity.class));
                return true;
            } else if (itemId == R.id.navigation_registration) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_prediction) {
                startActivity(new Intent(ProfileActivity.this, TabularActivity.class));
                return true;
            } else if (itemId == R.id.navigation_visualization) {
                startActivity(new Intent(ProfileActivity.this, Visualisation.class));
                return true;
            } else if (itemId == R.id.navigation_doctors_profile) {
                // We're already on the Profile page
                return true;
            }
            return false;
        });

        // Set the doctors_profile item as selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_doctors_profile);
    }

    private void setupMenuIcon() {
        menuIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ProfileActivity.this, menuIcon);
            popup.getMenuInflater().inflate(R.menu.header_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_settings) {
                    startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
                    return true;
                } else if (itemId == R.id.menu_about) {
                    startActivity(new Intent(ProfileActivity.this, AboutActivity.class));
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

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}