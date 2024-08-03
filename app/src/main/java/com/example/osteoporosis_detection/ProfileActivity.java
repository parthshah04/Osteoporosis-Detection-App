package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private ImageView profileImage;
    private EditText profileName, profileDesignation, profilePhone, profileEmail, profilePassword;
    private Button changeProfileImageButton, updatePasswordButton;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private String email;

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
        changeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Set click listener for updating password
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });
    }

    private void loadUserData(String email) {
        Cursor cursor = dbHelper.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            profileName.setText(cursor.getString(cursor.getColumnIndex("name")));
            profileDesignation.setText(cursor.getString(cursor.getColumnIndex("designation")));
            profilePhone.setText(cursor.getString(cursor.getColumnIndex("phone")));
            profileEmail.setText(cursor.getString(cursor.getColumnIndex("email")));

            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex("profile_photo"));
            if (imageBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                profileImage.setImageBitmap(bitmap);
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
}
