package com.example.osteoporosis_detection.data;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.osteoporosis_detection.util.EncryptionUtil;
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESIGNATION = "designation";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PROFILE_PHOTO = "profile_photo";
    private static final String SECRET_KEY = "5Tgb6Yhn7Ujm8Ik";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESIGNATION + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_PROFILE_PHOTO + " BLOB" + ")";
        db.execSQL(CREATE_USERS_TABLE);
        createInitialUser(db); // Create initial user
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void createInitialUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "Parth Shah");
        values.put(COLUMN_DESIGNATION, "MD Physicians");
        values.put(COLUMN_PHONE, "9987654321");
        values.put(COLUMN_EMAIL, "parth@gmail.com");
        try {
        values.put(COLUMN_PASSWORD, EncryptionUtil.encrypt("Parth@1234", SECRET_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        values.put(COLUMN_PROFILE_PHOTO, "app/src/main/res/drawable/profile.png"); // Add a path or URL to the profile photo

        db.insert(TABLE_USERS, null, values);
    }

    public Cursor getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
            if (passwordIndex != -1) {
                try {
                    String storedPassword = cursor.getString(passwordIndex);
                    String decryptedPassword = EncryptionUtil.decrypt(storedPassword, SECRET_KEY);
                    Log.d(TAG, "Stored password: " + decryptedPassword);
                    if (decryptedPassword.equals(password)) {
                        Log.d(TAG, "Password matched");
                        return cursor;
                    } else {
                        Log.d(TAG, "Password did not match");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?";
        return db.rawQuery(query, new String[]{email});
    }
    public void updateUserProfileImage(String email, byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_PHOTO, imageBytes);
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
    }
    public void updateUserPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put(COLUMN_PASSWORD, EncryptionUtil.encrypt(newPassword, SECRET_KEY)); // Encrypt the password
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
    }

    public void updateUserDetails(String email, String newName, String newDesignation, String newPhone, String newPassword, byte[] newProfilePhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        values.put(COLUMN_DESIGNATION, newDesignation);
        values.put(COLUMN_PHONE, newPhone);
        try {
            values.put(COLUMN_PASSWORD, EncryptionUtil.encrypt(newPassword, SECRET_KEY)); // Encrypt the password
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (newProfilePhoto != null) {
            values.put(COLUMN_PROFILE_PHOTO, newProfilePhoto);
        }
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
    }

    public void deleteUserByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
    }
}
