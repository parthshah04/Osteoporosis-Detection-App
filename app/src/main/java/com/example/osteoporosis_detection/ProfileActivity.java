package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.example.osteoporosis_detection.data.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

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
    private Button removeProfileImageButton;

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
        removeProfileImageButton = findViewById(R.id.removeProfileImageButton);
        //bottomNavigationView = findViewById(R.id.bottomNavigationView);
        menuIcon = findViewById(R.id.menuIcon);
        backIcon = findViewById(R.id.backIcon);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        addBottomNavigationView();
        setupBottomNavigation();

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

        removeProfileImageButton.setOnClickListener(v -> removeProfileImage());
        setupMenuIcon();
    }

    private void addBottomNavigationView() {
        BottomNavigationView bottomNavigationView = new BottomNavigationView(this);
        bottomNavigationView.setId(View.generateViewId());
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        bottomNavigationView.setBackground(getDrawable(R.drawable.bottom_nav_background));
        bottomNavigationView.setItemIconTintList(getColorStateList(R.color.bottom_nav_item_color));
        bottomNavigationView.setItemTextColor(getColorStateList(R.color.bottom_nav_item_color));
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.BOTTOM;

        ViewGroup rootView = findViewById(android.R.id.content);
        if (rootView instanceof FrameLayout) {
            ((FrameLayout) rootView).addView(bottomNavigationView, layoutParams);
        } else {
            // If the root view is not a FrameLayout, wrap it in one
            FrameLayout frameLayout = new FrameLayout(this);
            ViewGroup parent = (ViewGroup) rootView.getParent();
            int index = parent.indexOfChild(rootView);
            parent.removeView(rootView);
            parent.addView(frameLayout, index, rootView.getLayoutParams());
            frameLayout.addView(rootView);
            frameLayout.addView(bottomNavigationView, layoutParams);
        }

        this.bottomNavigationView = bottomNavigationView;
    }
    private void loadUserData(String email) {
        Log.d("ProfileActivity", "Loading user data for email: " + email);
        Cursor cursor = dbHelper.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
            int designationIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DESIGNATION);
            int phoneIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE);
            int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
            int profilePhotoIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PROFILE_PHOTO);

            Log.d("ProfileActivity", "Profile photo index: " + profilePhotoIndex);

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
                if (imageBytes != null && imageBytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profileImage.setImageBitmap(bitmap);
                } else {
                    Log.d("ProfileActivity", "No image bytes found, setting default image");

                    // Set the default image from resources
                    profileImage.setImageResource(R.drawable.profile);
                }
            } else {
                Log.d("ProfileActivity", "No profile photo column found, setting default image");

                // If the COLUMN_PROFILE_PHOTO doesn't exist in the cursor, set the default image
                profileImage.setImageResource(R.drawable.profile);
            }
            cursor.close();
        } else {
            Log.d("ProfileActivity", "User not found in database");

            Toast.makeText(this, R.string.error_user_not_found, Toast.LENGTH_SHORT).show();
            // Set the default image if user is not found
            profileImage.setImageResource(R.drawable.profile);
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

    private void removeProfileImage() {
        // Show a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Remove Profile Picture")
                .setMessage("Are you sure you want to remove your profile picture?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User confirmed, remove the profile picture
                    dbHelper.removeUserProfileImage(email);
                    // Set the default image
                    profileImage.setImageResource(R.drawable.profile);
                    Toast.makeText(ProfileActivity.this, "Profile picture removed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
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
        if (bottomNavigationView != null) {
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