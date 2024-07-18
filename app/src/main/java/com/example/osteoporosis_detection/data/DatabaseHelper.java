package com.example.osteoporosis_detection.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        values.put(COLUMN_NAME, "Initial User");
        values.put(COLUMN_DESIGNATION, "Admin");
        values.put(COLUMN_PHONE, "1234567890");
        values.put(COLUMN_EMAIL, "admin@gmail.com");
        values.put(COLUMN_PASSWORD, "password"); // Ideally, hash the password
        values.put(COLUMN_PROFILE_PHOTO, "app/src/main/res/drawable/profile.png"); // Add a path or URL to the profile photo

        db.insert(TABLE_USERS, null, values);
    }

    public Cursor getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        return db.rawQuery(query, new String[]{email, password});
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
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
        values.put(COLUMN_PASSWORD, newPassword);
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
    }

    public void updateUserDetails(String email, String newName, String newDesignation, String newPhone, String newPassword, byte[] newProfilePhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        values.put(COLUMN_DESIGNATION, newDesignation);
        values.put(COLUMN_PHONE, newPhone);
        values.put(COLUMN_PASSWORD, newPassword);
        if (newProfilePhoto != null) {
            values.put(COLUMN_PROFILE_PHOTO, newProfilePhoto);
        }
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
    }
}
