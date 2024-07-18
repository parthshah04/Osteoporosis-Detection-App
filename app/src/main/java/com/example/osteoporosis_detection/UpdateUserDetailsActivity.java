package com.example.osteoporosis_detection;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.osteoporosis_detection.data.DatabaseHelper;

public class UpdateUserDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the database helper
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // User details to be updated
        String userEmail = "admin@gmail.com"; // The email of the user to be updated
        String newName = "Parth Shah";
        String newDesignation = "MD Physicians";
        String newPhone = "9987654321";
        String newPassword = "Admin@1234"; // Ideally, hash the password
        byte[] newProfilePhoto = null; // You can convert a new image to byte array if needed

        // Update user details
        dbHelper.updateUserDetails(userEmail, newName, newDesignation, newPhone, newPassword, newProfilePhoto);

        // Finish the activity after updating the details
        finish();
    }
}
