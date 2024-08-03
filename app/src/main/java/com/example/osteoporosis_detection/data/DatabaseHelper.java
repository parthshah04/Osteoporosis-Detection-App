package com.example.osteoporosis_detection.data;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.osteoporosis_detection.util.EncryptionUtil;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 14; // Incremented version for schema change
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PREDICTIONS = "predictions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_TABULAR_PREDICTION = "tabular_prediction";
    public static final String COLUMN_IMAGE_PREDICTION = "image_prediction";
    public static final String COLUMN_FINAL_CONFIDENCE_SCORE = "final_confidence_score";
    public static final String COLUMN_RESULT = "result";
    public static final String COLUMN_MEDICATIONS = "medications";
    public static final String COLUMN_HORMONAL_CHANGES = "hormonal_changes";
    public static final String COLUMN_FAMILY_HISTORY = "family_history";
    public static final String COLUMN_BODY_WEIGHT = "body_weight";
    public static final String COLUMN_CALCIUM_INTAKE = "calcium_intake";
    public static final String COLUMN_VITAMIN_D_INTAKE = "vitamin_d_intake";
    public static final String COLUMN_PHYSICAL_ACTIVITY = "physical_activity";
    public static final String COLUMN_SMOKING = "smoking";
    public static final String COLUMN_ALCOHOL_CONSUMPTION = "alcohol_consumption";
    public static final String COLUMN_MEDICAL_CONDITIONS = "medical_conditions";
    public static final String COLUMN_PRIOR_FRACTURES = "prior_fractures";
    public static final String COLUMN_XRAY_IMAGE_PATH = "xray_image_path";
    private static final String COLUMN_DESIGNATION = "designation";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PROFILE_PHOTO = "profile_photo";
    private static final String SECRET_KEY = "5Tgb6Yhn7Ujm8Ik";
    public static final String COLUMN_HAS_PREDICTION = "has_prediction";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void log(String message) {
        Log.d(TAG, message);
        System.out.println(TAG + ": " + message);
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

        String CREATE_PREDICTIONS_TABLE = "CREATE TABLE " + TABLE_PREDICTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_AGE + " TEXT,"
                + COLUMN_TABULAR_PREDICTION + " TEXT,"
                + COLUMN_IMAGE_PREDICTION + " TEXT,"
                + COLUMN_RESULT + " TEXT,"
                + COLUMN_MEDICATIONS + " INTEGER,"
                + COLUMN_HORMONAL_CHANGES + " INTEGER,"
                + COLUMN_FAMILY_HISTORY + " INTEGER,"
                + COLUMN_BODY_WEIGHT + " INTEGER,"
                + COLUMN_CALCIUM_INTAKE + " INTEGER,"
                + COLUMN_VITAMIN_D_INTAKE + " INTEGER,"
                + COLUMN_PHYSICAL_ACTIVITY + " INTEGER,"
                + COLUMN_SMOKING + " INTEGER,"
                + COLUMN_ALCOHOL_CONSUMPTION + " INTEGER,"
                + COLUMN_MEDICAL_CONDITIONS + " INTEGER,"
                + COLUMN_PRIOR_FRACTURES + " INTEGER,"
                + COLUMN_XRAY_IMAGE_PATH + " TEXT,"
                + COLUMN_FINAL_CONFIDENCE_SCORE + " REAL,"
                + COLUMN_HAS_PREDICTION + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_PREDICTIONS_TABLE);

        createInitialUser(db); // Create initial user
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREDICTIONS);
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

    public long insertPredictionData(String name, String email, String age, Float tabularPrediction, Float imagePrediction, String result,
                                     int medications, int hormonalChanges, int familyHistory, int bodyWeight, int calciumIntake, int vitaminDIntake,
                                     int physicalActivity, int smoking, int alcoholConsumption, int medicalConditions, int priorFractures,
                                     String xrayImagePath, float finalConfidenceScore, boolean hasPrediction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_AGE, age);
            values.put(COLUMN_TABULAR_PREDICTION, tabularPrediction);
            values.put(COLUMN_IMAGE_PREDICTION, imagePrediction != null ? imagePrediction : 0f);
            values.put(COLUMN_RESULT, result);
            values.put(COLUMN_MEDICATIONS, medications);
            values.put(COLUMN_HORMONAL_CHANGES, hormonalChanges);
            values.put(COLUMN_FAMILY_HISTORY, familyHistory);
            values.put(COLUMN_BODY_WEIGHT, bodyWeight);
            values.put(COLUMN_CALCIUM_INTAKE, calciumIntake);
            values.put(COLUMN_VITAMIN_D_INTAKE, vitaminDIntake);
            values.put(COLUMN_PHYSICAL_ACTIVITY, physicalActivity);
            values.put(COLUMN_SMOKING, smoking);
            values.put(COLUMN_ALCOHOL_CONSUMPTION, alcoholConsumption);
            values.put(COLUMN_MEDICAL_CONDITIONS, medicalConditions);
            values.put(COLUMN_PRIOR_FRACTURES, priorFractures);
            values.put(COLUMN_XRAY_IMAGE_PATH, xrayImagePath);
            values.put(COLUMN_FINAL_CONFIDENCE_SCORE, finalConfidenceScore);
            values.put(COLUMN_HAS_PREDICTION, hasPrediction ? 1 : 0);

            long newRowId = db.insertOrThrow(TABLE_PREDICTIONS, null, values);
            Log.d(TAG, "New row inserted with ID: " + newRowId);
            return newRowId;
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting data: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public Cursor getAllPatients() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PREDICTIONS, null, null, null, null, null, null);
    }

    public boolean updatePatient(int id, String name, String email, String age) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_AGE, age);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        int rowsAffected = db.update(TABLE_PREDICTIONS, values, selection, selectionArgs);
        return rowsAffected > 0;
    }
    public Cursor getPatient(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_EMAIL,
                COLUMN_AGE,
                COLUMN_TABULAR_PREDICTION,
                COLUMN_IMAGE_PREDICTION,
                COLUMN_RESULT,
                COLUMN_MEDICATIONS,
                COLUMN_HORMONAL_CHANGES,
                COLUMN_FAMILY_HISTORY,
                COLUMN_BODY_WEIGHT,
                COLUMN_CALCIUM_INTAKE,
                COLUMN_VITAMIN_D_INTAKE,
                COLUMN_PHYSICAL_ACTIVITY,
                COLUMN_SMOKING,
                COLUMN_ALCOHOL_CONSUMPTION,
                COLUMN_MEDICAL_CONDITIONS,
                COLUMN_PRIOR_FRACTURES,
                COLUMN_XRAY_IMAGE_PATH,
                COLUMN_FINAL_CONFIDENCE_SCORE,
                COLUMN_HAS_PREDICTION
        };
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return db.query(TABLE_PREDICTIONS, columns, selection, selectionArgs, null, null, null);
    }

    public boolean deletePatient(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        int rowsDeleted = db.delete(TABLE_PREDICTIONS, selection, selectionArgs);
        return rowsDeleted > 0;
    }
    public Cursor searchPatients(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_NAME + " LIKE ? OR " + COLUMN_EMAIL + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%"};
        return db.query(TABLE_PREDICTIONS, null, selection, selectionArgs, null, null, null);
    }


    public boolean updatePredictionData(int id, String name, String email, String age,
                                        String tabularPrediction, String imagePrediction, String result, int medications,
                                        int hormonalChanges, int familyHistory,
                                        int bodyWeight, int calciumIntake, int vitaminDIntake,
                                        int physicalActivity, int smoking, int alcoholConsumption,
                                        int medicalConditions, int priorFractures, String xrayImagePath,
                                        float finalConfidenceScore, boolean hasPrediction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_TABULAR_PREDICTION, tabularPrediction);
        values.put(COLUMN_MEDICATIONS, medications);
        values.put(COLUMN_HORMONAL_CHANGES, hormonalChanges);
        values.put(COLUMN_FAMILY_HISTORY, familyHistory);
        values.put(COLUMN_BODY_WEIGHT, bodyWeight);
        values.put(COLUMN_CALCIUM_INTAKE, calciumIntake);
        values.put(COLUMN_VITAMIN_D_INTAKE, vitaminDIntake);
        values.put(COLUMN_PHYSICAL_ACTIVITY, physicalActivity);
        values.put(COLUMN_SMOKING, smoking);
        values.put(COLUMN_ALCOHOL_CONSUMPTION, alcoholConsumption);
        values.put(COLUMN_MEDICAL_CONDITIONS, medicalConditions);
        values.put(COLUMN_PRIOR_FRACTURES, priorFractures);
        values.put(COLUMN_XRAY_IMAGE_PATH, xrayImagePath);
        values.put(COLUMN_RESULT, result);
        values.put(COLUMN_IMAGE_PREDICTION, imagePrediction);
        values.put(COLUMN_FINAL_CONFIDENCE_SCORE, finalConfidenceScore);
        values.put(COLUMN_HAS_PREDICTION, hasPrediction ? 1 : 0);

        int rowsAffected = db.update(TABLE_PREDICTIONS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    public void deleteUserByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
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

    public int getTotalPatients() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PREDICTIONS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    public void checkDataInsertion() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PREDICTIONS;
        Cursor cursor = db.rawQuery(query, null);

        log("Total records in predictions table: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                float finalScore = cursor.getFloat(cursor.getColumnIndex(COLUMN_FINAL_CONFIDENCE_SCORE));
                float imageScore = cursor.getFloat(cursor.getColumnIndex(COLUMN_IMAGE_PREDICTION));
                int hasPrediction = cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_PREDICTION));

                log("Record " + id + ": Final Score = " + finalScore +
                        ", Image Score = " + imageScore +
                        ", Has Prediction = " + hasPrediction);
            } while (cursor.moveToNext());
        } else {
            log("No records found in predictions table");
        }
        cursor.close();
    }
    public int[] getOsteoporosisCountBasedOnAverage(float threshold) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                COLUMN_ID + ", " +
                COLUMN_FINAL_CONFIDENCE_SCORE + ", " +
                COLUMN_IMAGE_PREDICTION +
                " FROM " + TABLE_PREDICTIONS +
                " WHERE " + COLUMN_HAS_PREDICTION + " = 1";

        log("Executing query: " + query);
        log("Threshold: " + threshold);

        Cursor cursor = db.rawQuery(query, null);

        int[] counts = new int[2]; // [0] for without osteoporosis, [1] for with osteoporosis

        log("Cursor count: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                float finalScore = cursor.getFloat(cursor.getColumnIndex(COLUMN_FINAL_CONFIDENCE_SCORE));
                float imageScore = cursor.getFloat(cursor.getColumnIndex(COLUMN_IMAGE_PREDICTION));

                // Use the higher of the two scores
                float highestScore = finalScore;

                log("Patient " + id + " - Final Score: " + finalScore + ", Image Score: " + imageScore + ", Highest Score: " + highestScore);

                if (highestScore > threshold) {
                    counts[1]++; // With osteoporosis
                    log("Patient " + id + " classified as having osteoporosis");
                } else {
                    counts[0]++; // Without osteoporosis
                    log("Patient " + id + " classified as not having osteoporosis");
                }

            } while (cursor.moveToNext());
        } else {
            log("Cursor is empty");
        }
        cursor.close();

        log("Final counts - With Osteoporosis: " + counts[1] + ", Without Osteoporosis: " + counts[0]);
        return counts;
    }

    public void checkRawData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PREDICTIONS;
        Cursor cursor = db.rawQuery(query, null);

        log("Total records in predictions table: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                float finalScore = cursor.getFloat(cursor.getColumnIndex(COLUMN_FINAL_CONFIDENCE_SCORE));
                float imageScore = cursor.getFloat(cursor.getColumnIndex(COLUMN_IMAGE_PREDICTION));
                int hasPrediction = cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_PREDICTION));

                log("Record " + id + ":");
                log("  Final Score: " + finalScore);
                log("  Image Score: " + imageScore);
                log("  Has Prediction: " + hasPrediction);
            } while (cursor.moveToNext());
        } else {
            log("No records found in predictions table");
        }
        cursor.close();
    }


    public Map<String, Integer> getAgeGroupDistribution() {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Integer> ageGroupCounts = new HashMap<>();
        String query = "SELECT " +
                "CASE " +
                "WHEN CAST(" + COLUMN_AGE + " AS INTEGER) BETWEEN 0 AND 20 THEN '0-20' " +
                "WHEN CAST(" + COLUMN_AGE + " AS INTEGER) BETWEEN 21 AND 40 THEN '21-40' " +
                "WHEN CAST(" + COLUMN_AGE + " AS INTEGER) BETWEEN 41 AND 60 THEN '41-60' " +
                "WHEN CAST(" + COLUMN_AGE + " AS INTEGER) BETWEEN 61 AND 80 THEN '61-80' " +
                "ELSE '80+' END AS age_group, " +
                "COUNT(*) AS count " +
                "FROM " + TABLE_PREDICTIONS +
                " GROUP BY age_group";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String ageGroup = cursor.getString(0);
                int count = cursor.getInt(1);
                ageGroupCounts.put(ageGroup, count);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ageGroupCounts;
    }

    public float getAverageTabularPrediction() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT AVG(CAST(" + COLUMN_FINAL_CONFIDENCE_SCORE + " AS FLOAT)) FROM " + TABLE_PREDICTIONS +
                " WHERE " + COLUMN_HAS_PREDICTION + " = 1";
        Cursor cursor = db.rawQuery(query, null);
        float avg = 0;
        if (cursor.moveToFirst()) {
            avg = cursor.getFloat(0);
        }
        cursor.close();
        return avg;
    }

    public Cursor getAllPredictions() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + ", " +
                COLUMN_FINAL_CONFIDENCE_SCORE + ", " +
                COLUMN_IMAGE_PREDICTION +
                " FROM " + TABLE_PREDICTIONS +
                " WHERE " + COLUMN_HAS_PREDICTION + " = 1";
        return db.rawQuery(query, null);
    }

    public Map<String, Object> getPatientPredictionData(int patientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_TABULAR_PREDICTION + ", " +
                COLUMN_IMAGE_PREDICTION + ", " +
                COLUMN_FINAL_CONFIDENCE_SCORE + ", " +
                COLUMN_HAS_PREDICTION +
                " FROM " + TABLE_PREDICTIONS +
                " WHERE " + COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(patientId)});
        Map<String, Object> predictionData = new HashMap<>();

        if (cursor.moveToFirst()) {
            predictionData.put("tabularPrediction", cursor.getString(0));
            predictionData.put("imagePrediction", cursor.getString(1));
            predictionData.put("finalConfidenceScore", cursor.getFloat(2));
            predictionData.put("hasPrediction", cursor.getInt(3) == 1);
        }
        cursor.close();
        return predictionData;
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
    /*public void updateXrayImage(int patientId, byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_XRAY_IMAGE, imageBytes);
        db.update(TABLE_PREDICTIONS, values, COLUMN_ID + "=?", new String[]{String.valueOf(patientId)});
    }*/
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
}