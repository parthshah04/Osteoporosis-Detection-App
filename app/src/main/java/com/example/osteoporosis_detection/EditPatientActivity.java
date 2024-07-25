package com.example.osteoporosis_detection;

import static android.content.ContentValues.TAG;
import android.util.Base64;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.osteoporosis_detection.data.DatabaseHelper;

public class EditPatientActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextAge;
    private Spinner spinnerMedications, spinnerHormonalChanges, spinnerFamilyHistory,
            spinnerBodyWeight, spinnerCalciumIntake, spinnerVitaminDIntake,
            spinnerPhysicalActivity, spinnerSmoking, spinnerAlcoholConsumption,
            spinnerMedicalConditions, spinnerPriorFractures;
    private Button buttonSave, buttonCancel, buttonSelectImage, buttonPredict;
    private DatabaseHelper db;
    private int patientId;
    private ImageView imageViewXray;
    private Uri xrayImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final String TAG = "EditPatientActivity";
    private TextView textViewResult, textViewImagePrediction, textViewTabularPrediction;
    private ProgressBar progressBarResult;
    private Interpreter tfliteVGG19;
    private Interpreter tfliteTabular;
    private Bitmap xRayImage;
    private String xrayImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);

        initializeViews();
        setupSpinners();
        initializeViews();
        setupSpinners();
        setupImagePicker();
        textViewResult = findViewById(R.id.textViewResult);
        textViewImagePrediction = findViewById(R.id.textViewImagePrediction);
        textViewTabularPrediction = findViewById(R.id.textViewTabularPrediction);
        progressBarResult = findViewById(R.id.progressBarResult);
        buttonPredict.setOnClickListener(v -> makePrediction());

// Load models
        try {
            tfliteVGG19 = new Interpreter(loadModelFile("vgg19_finetuned_best_quantized.tflite"));
            tfliteTabular = new Interpreter(loadModelFile("Tabular_Model.tflite"));
            Log.d(TAG, "Models loaded successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Error loading models: ", e);
        }

        db = new DatabaseHelper(this);

        patientId = getIntent().getIntExtra("PATIENT_ID", -1);
        if (patientId == -1) {
            Log.e(TAG, "Invalid patient ID received");
            Toast.makeText(this, "Error: Patient not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPatientData();

        buttonSave.setOnClickListener(v -> savePatientData());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private MappedByteBuffer loadModelFile(String modelFileName) throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd(modelFileName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }



    private void makePrediction() {
        try {
            // Check if models are loaded
            if (tfliteTabular == null || tfliteVGG19 == null) {
                Log.e(TAG, "Models not loaded.");
                textViewResult.setText("Error: Models not loaded.");
                return;
            }

            // Check if Age input is empty
            if (editTextAge.getText().toString().isEmpty()) {
                textViewResult.setText("Please enter age.");
                return;
            }

            // Extract and preprocess input data
            float[] tabularInput = new float[12];
            tabularInput[0] = Float.parseFloat(editTextAge.getText().toString());
            tabularInput[1] = spinnerBodyWeight.getSelectedItemPosition();
            tabularInput[2] = spinnerSmoking.getSelectedItemPosition();
            tabularInput[3] = spinnerMedications.getSelectedItemPosition();
            tabularInput[4] = spinnerHormonalChanges.getSelectedItemPosition();
            tabularInput[5] = spinnerFamilyHistory.getSelectedItemPosition();
            tabularInput[6] = spinnerCalciumIntake.getSelectedItemPosition();
            tabularInput[7] = spinnerVitaminDIntake.getSelectedItemPosition();
            tabularInput[8] = spinnerPhysicalActivity.getSelectedItemPosition();
            tabularInput[9] = spinnerAlcoholConsumption.getSelectedItemPosition();
            tabularInput[10] = spinnerMedicalConditions.getSelectedItemPosition();
            tabularInput[11] = spinnerPriorFractures.getSelectedItemPosition();

            // Perform inference using tabular model
            Log.d(TAG, "Starting tabular prediction");
            float tabularPrediction = predictTabularModel(tabularInput);
            Log.d(TAG, "Tabular prediction: " + tabularPrediction);

            // Perform inference using VGG-19 model
            if (xrayImagePath == null || xrayImagePath.isEmpty()) {
                Log.e(TAG, "No image selected.");
                textViewResult.setText("Please select an image.");
                return;
            }
            Log.d(TAG, "Starting image prediction");
            float imagePrediction = predictImageModel(xrayImagePath);
            Log.d(TAG, "Image prediction: " + imagePrediction);

            // Combine predictions using confidence score
            float finalConfidenceScore = (tabularPrediction + imagePrediction) / 2;
            String resultText = "The anticipated rate of osteoporosis occurrence is currently: " + (finalConfidenceScore * 100) + "%";
            textViewResult.setText(resultText);

            // Update individual predictions
            textViewImagePrediction.setText("Prediction of Image Data: " + (imagePrediction * 100) + "%");
            textViewTabularPrediction.setText("Prediction of Tabular Data: " + (tabularPrediction * 100) + "%");

            // Update progress bar
            progressBarResult.setProgress((int) (finalConfidenceScore * 100));

        } catch (Exception e) {
            Log.e(TAG, "Error making prediction: ", e);
            textViewResult.setText("Error in prediction: " + e.getMessage());
        }
    }




    private float predictTabularModel(float[] inputFeatures) {
        float[][] inputBuffer = new float[1][12];
        inputBuffer[0] = inputFeatures;
        float[][] outputBuffer = new float[1][1];
        tfliteTabular.run(inputBuffer, outputBuffer);
        return outputBuffer[0][0];
    }

    private float predictImageModel(String xrayImagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(xrayImagePath);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        byteBuffer.rewind();

        int[] intValues = new int[224 * 224];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        for (int pixel : intValues) {
            byteBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f);
            byteBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);
            byteBuffer.putFloat((pixel & 0xFF) / 255.0f);
        }

        float[][] outputBuffer = new float[1][2];
        tfliteVGG19.run(byteBuffer, outputBuffer);
        return outputBuffer[0][1];
    }

    private void initializeViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAge = findViewById(R.id.editTextAge);
        spinnerMedications = findViewById(R.id.spinnerMedications);
        spinnerHormonalChanges = findViewById(R.id.spinnerHormonalChanges);
        spinnerFamilyHistory = findViewById(R.id.spinnerFamilyHistory);
        spinnerBodyWeight = findViewById(R.id.spinnerBodyWeight);
        spinnerCalciumIntake = findViewById(R.id.spinnerCalciumIntake);
        spinnerVitaminDIntake = findViewById(R.id.spinnerVitaminDIntake);
        spinnerPhysicalActivity = findViewById(R.id.spinnerPhysicalActivity);
        spinnerSmoking = findViewById(R.id.spinnerSmoking);
        spinnerAlcoholConsumption = findViewById(R.id.spinnerAlcoholConsumption);
        spinnerMedicalConditions = findViewById(R.id.spinnerMedicalConditions);
        spinnerPriorFractures = findViewById(R.id.spinnerPriorFractures);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        imageViewXray = findViewById(R.id.imageViewXray);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);

        buttonSelectImage.setOnClickListener(v -> openImagePicker());
        buttonPredict = findViewById(R.id.buttonPredict);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        xrayImageUri = result.getData().getData();
                        try {
                            xRayImage = MediaStore.Images.Media.getBitmap(getContentResolver(), xrayImageUri);
                            imageViewXray.setImageBitmap(xRayImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // Update the imagePickerLauncher


    private void setupSpinners() {
        // Medications
        ArrayAdapter<String> medicationsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.medications_options));
        spinnerMedications.setAdapter(medicationsAdapter);

        // Hormonal Changes
        ArrayAdapter<String> hormonalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.hormonal_changes_options));
        spinnerHormonalChanges.setAdapter(hormonalAdapter);

        // Family History
        ArrayAdapter<String> familyHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.family_history_options));
        spinnerFamilyHistory.setAdapter(familyHistoryAdapter);

        // Body Weight
        ArrayAdapter<String> bodyWeightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.body_weight_options));
        spinnerBodyWeight.setAdapter(bodyWeightAdapter);

        // Calcium Intake
        ArrayAdapter<String> calciumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.calcium_intake_options));
        spinnerCalciumIntake.setAdapter(calciumAdapter);

        // Vitamin D Intake
        ArrayAdapter<String> vitaminDAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.vitamin_d_intake_options));
        spinnerVitaminDIntake.setAdapter(vitaminDAdapter);

        // Physical Activity
        ArrayAdapter<String> physicalActivityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.physical_activity_options));
        spinnerPhysicalActivity.setAdapter(physicalActivityAdapter);

        // Smoking
        ArrayAdapter<String> smokingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.smoking_options));
        spinnerSmoking.setAdapter(smokingAdapter);

        // Alcohol Consumption
        ArrayAdapter<String> alcoholAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.alcohol_consumption_options));
        spinnerAlcoholConsumption.setAdapter(alcoholAdapter);

        // Medical Conditions
        ArrayAdapter<String> medicalConditionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.medical_conditions_options));
        spinnerMedicalConditions.setAdapter(medicalConditionsAdapter);

        // Prior Fractures
        ArrayAdapter<String> priorFracturesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.prior_fractures_options));
        spinnerPriorFractures.setAdapter(priorFracturesAdapter);
    }


    private void loadPatientData() {
        Cursor cursor = null;
        cursor = db.getPatient(patientId);
        if (cursor != null && cursor.moveToFirst()) {
            editTextName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
            editTextEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
            editTextAge.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AGE)));

            // Set spinner selections
            spinnerMedications.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEDICATIONS)));
            spinnerHormonalChanges.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HORMONAL_CHANGES)));
            spinnerFamilyHistory.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAMILY_HISTORY)));
            spinnerBodyWeight.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BODY_WEIGHT)));
            spinnerCalciumIntake.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALCIUM_INTAKE)));
            spinnerVitaminDIntake.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VITAMIN_D_INTAKE)));
            spinnerPhysicalActivity.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHYSICAL_ACTIVITY)));
            spinnerSmoking.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SMOKING)));
            spinnerAlcoholConsumption.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALCOHOL_CONSUMPTION)));
            spinnerMedicalConditions.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEDICAL_CONDITIONS)));
            spinnerPriorFractures.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRIOR_FRACTURES)));

            // Load X-ray image
            xrayImagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_XRAY_IMAGE_PATH));
            if (xrayImagePath != null) {
                xRayImage = BitmapFactory.decodeFile(xrayImagePath);
                imageViewXray.setImageBitmap(xRayImage);
            } else {
                imageViewXray.setImageResource(R.drawable.about);
            }

            cursor.close();
        }
    }

    private void savePatientData() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();


        if (name.isEmpty() || email.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect all the data
        int medications = spinnerMedications.getSelectedItemPosition();
        int hormonalChanges = spinnerHormonalChanges.getSelectedItemPosition();
        int familyHistory = spinnerFamilyHistory.getSelectedItemPosition();
        int bodyWeight = spinnerBodyWeight.getSelectedItemPosition();
        int calciumIntake = spinnerCalciumIntake.getSelectedItemPosition();
        int vitaminDIntake = spinnerVitaminDIntake.getSelectedItemPosition();
        int physicalActivity = spinnerPhysicalActivity.getSelectedItemPosition();
        int smoking = spinnerSmoking.getSelectedItemPosition();
        int alcoholConsumption = spinnerAlcoholConsumption.getSelectedItemPosition();
        int medicalConditions = spinnerMedicalConditions.getSelectedItemPosition();
        int priorFractures = spinnerPriorFractures.getSelectedItemPosition();
        String xrayImagePath = null;
        if (xRayImage != null) {
            xrayImagePath = saveImageToInternalStorage(xRayImage);
        }

        // Recalculate the tabular prediction
        String tabularPrediction = recalculatePrediction(age, medications, hormonalChanges,
                familyHistory, bodyWeight, calciumIntake, vitaminDIntake, physicalActivity,
                smoking, alcoholConsumption, medicalConditions, priorFractures, xrayImagePath);

        boolean updated = db.updatePredictionData(
                patientId,
                name,
                email,
                age,
                tabularPrediction,
                medications,
                hormonalChanges,
                familyHistory,
                bodyWeight,
                calciumIntake,
                vitaminDIntake,
                physicalActivity,
                smoking,
                alcoholConsumption,
                medicalConditions,
                priorFractures,
                xrayImagePath
        );

        if (updated) {
            Toast.makeText(this, "Patient updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update patient", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        // Create a file to save the image
        File directory = getApplicationContext().getFilesDir();
        String fileName = "xray_" + System.currentTimeMillis() + ".png";
        File file = new File(directory, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file.getAbsolutePath();
    }
    private String recalculatePrediction(String age, int medications, int hormonalChanges,
                                         int familyHistory, int bodyWeight, int calciumIntake, int vitaminDIntake,
                                         int physicalActivity, int smoking, int alcoholConsumption,
                                         int medicalConditions, int priorFractures, String xrayImagePath) {
        try {
            // Check if models are loaded
            if (tfliteTabular == null || tfliteVGG19 == null) {
                Log.e(TAG, "Models not loaded.");
                return "Error: Models not loaded.";
            }

            // Extract and preprocess input data
            float[] tabularInput = new float[12];
            tabularInput[0] = Float.parseFloat(age);
            tabularInput[1] = bodyWeight;
            tabularInput[2] = smoking;
            tabularInput[3] = medications;
            tabularInput[4] = hormonalChanges;
            tabularInput[5] = familyHistory;
            tabularInput[6] = calciumIntake;
            tabularInput[7] = vitaminDIntake;
            tabularInput[8] = physicalActivity;
            tabularInput[9] = alcoholConsumption;
            tabularInput[10] = medicalConditions;
            tabularInput[11] = priorFractures;

            // Perform inference using tabular model
            float tabularPrediction = predictTabularModel(tabularInput);
            Log.d(TAG, "Tabular prediction: " + tabularPrediction);

            // Perform inference using VGG-19 model
            if (xRayImage == null) {
                Log.e(TAG, "No image selected.");
                return "Please select an image.";
            }
            float imagePrediction = predictImageModel(xrayImagePath);
            Log.d(TAG, "Image prediction: " + imagePrediction);

            // Combine predictions using confidence score
            float finalConfidenceScore = (tabularPrediction + imagePrediction) / 2;
            String resultText = "The anticipated rate of osteoporosis occurrence is currently: " + (finalConfidenceScore * 100) + "%";

            // Update individual predictions
            textViewImagePrediction.setText("Prediction of Image Data: " + (imagePrediction * 100) + "%");
            textViewTabularPrediction.setText("Prediction of Tabular Data: " + (tabularPrediction * 100) + "%");

            // Update progress bar
            progressBarResult.setProgress((int) (finalConfidenceScore * 100));

            return resultText;

        } catch (Exception e) {
            Log.e(TAG, "Error making prediction: ", e);
            return "Error in prediction.";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tfliteVGG19 != null) {
            tfliteVGG19.close();
        }
        if (tfliteTabular != null) {
            tfliteTabular.close();
        }
    }
}