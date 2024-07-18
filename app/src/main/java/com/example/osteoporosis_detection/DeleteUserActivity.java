package com.example.osteoporosis_detection;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.osteoporosis_detection.data.DatabaseHelper;

public class DeleteUserActivity extends AppCompatActivity {

    private static final String TAG = "DeleteUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the database helper
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // User email to be deleted
        String userEmail = "parth@gmail.com"; // The email of the user to be deleted

        // Delete the user
        dbHelper.deleteUserByEmail(userEmail);
        Log.d(TAG, "User with email " + userEmail + " deleted");

        // Finish the activity after deleting the user
        finish();
    }
}
