package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

public class EditPatientActivity extends AppCompatActivity {

    private static final String TAG = "EditPatientActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName, editTextEmail, editTextAge;
    private Spinner spinnerMedications, spinnerHormonalChanges, spinnerFamilyHistory,
            spinnerBodyWeight, spinnerCalciumIntake, spinnerVitaminDIntake,
            spinnerPhysicalActivity, spinnerSmoking, spinnerAlcoholConsumption,
            spinnerMedicalConditions, spinnerPriorFractures;
    private Button buttonSave, buttonCancel, buttonPredict, buttonSelectImage, buttonDelete;
    private ImageView imageViewXray;
    private TextView textViewResult, textViewImagePrediction, textViewTabularPrediction, textViewNoPrediction;
    private ProgressBar progressBarResult;

    private DatabaseHelper db;
    private int patientId;
    private Bitmap xRayImage;
    private String xrayImagePath;
    private Interpreter tfliteVGG19;
    private Interpreter tfliteTabular;
    private boolean hasPrediction;

    private String originalName, originalEmail, originalAge;
    private int originalMedications, originalHormonalChanges, originalFamilyHistory,
            originalBodyWeight, originalCalciumIntake, originalVitaminDIntake,
            originalPhysicalActivity, originalSmoking, originalAlcoholConsumption,
            originalMedicalConditions, originalPriorFractures;
    private String originalXrayImagePath;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private ImageView backIcon, menuIcon;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);

        initializeViews();
        setupToolbar();
        setupSpinners();
        setupImagePicker();
        loadModels();

        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        patientId = getIntent().getIntExtra("PATIENT_ID", -1);
        if (patientId == -1) {
            Log.e(TAG, "Invalid patient ID received");
            Toast.makeText(this, "Error: Patient not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPatientData();
        addChangeListeners();

        buttonPredict.setOnClickListener(v -> makePrediction());
        buttonSave.setOnClickListener(v -> savePatientData());
        buttonCancel.setOnClickListener(v -> finish());
        buttonSelectImage.setOnClickListener(v -> openImagePicker());
        buttonDelete.setOnClickListener(v -> confirmDelete());
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
        buttonPredict = findViewById(R.id.buttonPredict);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        imageViewXray = findViewById(R.id.imageViewXray);
        textViewResult = findViewById(R.id.textViewResult);
        textViewImagePrediction = findViewById(R.id.textViewImagePrediction);
        textViewTabularPrediction = findViewById(R.id.textViewTabularPrediction);
        textViewNoPrediction = findViewById(R.id.textViewNoPrediction);
        progressBarResult = findViewById(R.id.progressBarResult);
        buttonDelete = findViewById(R.id.buttonDelete);
        backIcon = findViewById(R.id.backIcon);
        menuIcon = findViewById(R.id.menuIcon);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(EditPatientActivity.this, TabularActivity.class);
            startActivity(intent);
            finish();
        });

        menuIcon.setOnClickListener(v -> showMenu());
    }

    private void showMenu() {
        PopupMenu popup = new PopupMenu(EditPatientActivity.this, menuIcon);
        popup.getMenuInflater().inflate(R.menu.header_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_settings) {
                startActivity(new Intent(EditPatientActivity.this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.menu_about) {
                startActivity(new Intent(EditPatientActivity.this, AboutActivity.class));
                return true;
            } else if (itemId == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(EditPatientActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupSpinners() {
        setupSpinner(spinnerMedications, R.array.medications_options);
        setupSpinner(spinnerHormonalChanges, R.array.hormonal_changes_options);
        setupSpinner(spinnerFamilyHistory, R.array.family_history_options);
        setupSpinner(spinnerBodyWeight, R.array.body_weight_options);
        setupSpinner(spinnerCalciumIntake, R.array.calcium_intake_options);
        setupSpinner(spinnerVitaminDIntake, R.array.vitamin_d_intake_options);
        setupSpinner(spinnerPhysicalActivity, R.array.physical_activity_options);
        setupSpinner(spinnerSmoking, R.array.smoking_options);
        setupSpinner(spinnerAlcoholConsumption, R.array.alcohol_consumption_options);
        setupSpinner(spinnerMedicalConditions, R.array.medical_conditions_options);
        setupSpinner(spinnerPriorFractures, R.array.prior_fractures_options);
    }

    private void setupSpinner(Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            xRayImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageViewXray.setImageBitmap(xRayImage);
                            xrayImagePath = copyImageToPrivateStorage(selectedImageUri);
                            checkForChanges();
                            Log.d(TAG, "New image selected. Path: " + xrayImagePath);
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading image: ", e);
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private String copyImageToPrivateStorage(Uri sourceUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(sourceUri);
        String fileName = "xray_" + System.currentTimeMillis() + ".jpg";
        File destFile = new File(getFilesDir(), fileName);

        try (FileOutputStream outputStream = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        inputStream.close();
        return destFile.getAbsolutePath();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void loadModels() {
        try {
            tfliteVGG19 = new Interpreter(loadModelFile("vgg19_finetuned_best_quantized.tflite"));
            tfliteTabular = new Interpreter(loadModelFile("Tabular_Model.tflite"));
            Log.d(TAG, "Models loaded successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Error loading models: ", e);
        }
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
            if (tfliteTabular == null || tfliteVGG19 == null) {
                Log.e(TAG, "Models not loaded.");
                textViewResult.setText("Error: Models not loaded.");
                return;
            }

            if (editTextAge.getText().toString().isEmpty()) {
                textViewResult.setText("Please enter age.");
                return;
            }

            float[] tabularInput = extractTabularInput();
            float tabularPrediction = predictTabularModel(tabularInput);
            Log.d(TAG, "Tabular prediction: " + tabularPrediction);

            if (xrayImagePath == null || xrayImagePath.isEmpty()) {
                Log.e(TAG, "No image selected.");
                textViewResult.setText("Please select an image.");
                return;
            }

            float imagePrediction = predictImageModel(xrayImagePath);
            Log.d(TAG, "Image prediction: " + imagePrediction);

            float finalConfidenceScore = (tabularPrediction + imagePrediction) / 2;
            String resultText = "The anticipated rate of osteoporosis occurrence is currently: " + String.format("%.2f", finalConfidenceScore * 100) + "%";
            textViewResult.setText(resultText);

            textViewImagePrediction.setText("Image Data Prediction: " + (imagePrediction > 0.5 ? "Osteoporosis" : "Normal"));
            textViewTabularPrediction.setText("Tabular Data Prediction: " + (tabularPrediction > 0.5 ? "Osteoporosis" : "Normal"));

            progressBarResult.setProgress((int) (finalConfidenceScore * 100));

            hasPrediction = true;
            updatePredictionViews();

        } catch (Exception e) {
            Log.e(TAG, "Error making prediction: ", e);
            textViewResult.setText("Error in prediction: " + e.getMessage());
        }
    }

    private float[] extractTabularInput() {
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
        return tabularInput;
    }

    private float predictTabularModel(float[] inputFeatures) {
        float[][] inputBuffer = new float[1][12];
        inputBuffer[0] = inputFeatures;
        float[][] outputBuffer = new float[1][1];
        tfliteTabular.run(inputBuffer, outputBuffer);
        return outputBuffer[0][0];
    }

    private float predictImageModel(String xrayImagePath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(xrayImagePath);
            if (bitmap == null) {
                Log.e(TAG, "Failed to decode image file");
                return 0;
            }
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
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
        } catch (Exception e) {
            Log.e(TAG, "Error predicting image model: ", e);
            return 0;
        }
    }

    private void loadPatientData() {
        Cursor cursor = db.getPatient(patientId);
        if (cursor != null && cursor.moveToFirst()) {
            editTextName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
            editTextEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
            editTextAge.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AGE)));

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

            xrayImagePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_XRAY_IMAGE_PATH));
            if (xrayImagePath != null && !xrayImagePath.isEmpty()) {
                xRayImage = BitmapFactory.decodeFile(xrayImagePath);
                imageViewXray.setImageBitmap(xRayImage);
            }

            hasPrediction = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HAS_PREDICTION)) == 1;
            if (hasPrediction) {
                textViewResult.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESULT)));
                textViewImagePrediction.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_PREDICTION)));
                textViewTabularPrediction.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TABULAR_PREDICTION)));
                progressBarResult.setProgress((int)(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FINAL_CONFIDENCE_SCORE)) * 100));
            }

            updatePredictionViews();

            cursor.close();

            saveOriginalValues();
        }
    }

    private void saveOriginalValues() {
        originalName = editTextName.getText().toString();
        originalEmail = editTextEmail.getText().toString();
        originalAge = editTextAge.getText().toString();
        originalMedications = spinnerMedications.getSelectedItemPosition();
        originalHormonalChanges = spinnerHormonalChanges.getSelectedItemPosition();
        originalFamilyHistory = spinnerFamilyHistory.getSelectedItemPosition();
        originalBodyWeight = spinnerBodyWeight.getSelectedItemPosition();
        originalCalciumIntake = spinnerCalciumIntake.getSelectedItemPosition();
        originalVitaminDIntake = spinnerVitaminDIntake.getSelectedItemPosition();
        originalPhysicalActivity = spinnerPhysicalActivity.getSelectedItemPosition();
        originalSmoking = spinnerSmoking.getSelectedItemPosition();
        originalAlcoholConsumption = spinnerAlcoholConsumption.getSelectedItemPosition();
        originalMedicalConditions = spinnerMedicalConditions.getSelectedItemPosition();
        originalPriorFractures = spinnerPriorFractures.getSelectedItemPosition();
        originalXrayImagePath = xrayImagePath;
    }

    private void addChangeListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkForChanges();
            }
        };

        editTextName.addTextChangedListener(textWatcher);
        editTextEmail.addTextChangedListener(textWatcher);
        editTextAge.addTextChangedListener(textWatcher);

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkForChanges();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerMedications.setOnItemSelectedListener(spinnerListener);
        spinnerHormonalChanges.setOnItemSelectedListener(spinnerListener);
        spinnerFamilyHistory.setOnItemSelectedListener(spinnerListener);
        spinnerBodyWeight.setOnItemSelectedListener(spinnerListener);
        spinnerCalciumIntake.setOnItemSelectedListener(spinnerListener);
        spinnerVitaminDIntake.setOnItemSelectedListener(spinnerListener);
        spinnerPhysicalActivity.setOnItemSelectedListener(spinnerListener);
        spinnerSmoking.setOnItemSelectedListener(spinnerListener);
        spinnerAlcoholConsumption.setOnItemSelectedListener(spinnerListener);
        spinnerMedicalConditions.setOnItemSelectedListener(spinnerListener);
        spinnerPriorFractures.setOnItemSelectedListener(spinnerListener);
    }

    private void checkForChanges() {
        boolean hasChanges = !originalName.equals(editTextName.getText().toString()) ||
                !originalEmail.equals(editTextEmail.getText().toString()) ||
                !originalAge.equals(editTextAge.getText().toString()) ||
                originalMedications != spinnerMedications.getSelectedItemPosition() ||
                originalHormonalChanges != spinnerHormonalChanges.getSelectedItemPosition() ||
                originalFamilyHistory != spinnerFamilyHistory.getSelectedItemPosition() ||
                originalBodyWeight != spinnerBodyWeight.getSelectedItemPosition() ||
                originalCalciumIntake != spinnerCalciumIntake.getSelectedItemPosition() ||
                originalVitaminDIntake != spinnerVitaminDIntake.getSelectedItemPosition() ||
                originalPhysicalActivity != spinnerPhysicalActivity.getSelectedItemPosition() ||
                originalSmoking != spinnerSmoking.getSelectedItemPosition() ||
                originalAlcoholConsumption != spinnerAlcoholConsumption.getSelectedItemPosition() ||
                originalMedicalConditions != spinnerMedicalConditions.getSelectedItemPosition() ||
                originalPriorFractures != spinnerPriorFractures.getSelectedItemPosition() ||
                !Objects.equals(originalXrayImagePath, xrayImagePath);

        buttonSave.setEnabled(hasChanges);
    }

    private void updatePredictionViews() {
        if (hasPrediction) {
            textViewResult.setVisibility(View.VISIBLE);
            textViewImagePrediction.setVisibility(View.VISIBLE);
            textViewTabularPrediction.setVisibility(View.VISIBLE);
            progressBarResult.setVisibility(View.VISIBLE);
            textViewNoPrediction.setVisibility(View.GONE);
            buttonPredict.setText("Update Prediction");
        } else {
            textViewResult.setVisibility(View.GONE);
            textViewImagePrediction.setVisibility(View.GONE);
            textViewTabularPrediction.setVisibility(View.GONE);
            progressBarResult.setVisibility(View.GONE);
            textViewNoPrediction.setVisibility(View.VISIBLE);
            textViewNoPrediction.setText("No prediction has been made yet.");
            buttonPredict.setText("Make Prediction");
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

        String result = textViewResult.getText().toString();
        String imagePrediction = textViewImagePrediction.getText().toString();
        String tabularPrediction = textViewTabularPrediction.getText().toString();
        float finalConfidenceScore = progressBarResult.getProgress() / 100f;

        boolean updated = db.updatePredictionData(
                patientId, name, email, age, tabularPrediction, imagePrediction, result,
                medications, hormonalChanges, familyHistory, bodyWeight, calciumIntake,
                vitaminDIntake, physicalActivity, smoking, alcoholConsumption,
                medicalConditions, priorFractures, xrayImagePath, finalConfidenceScore, hasPrediction
        );

        if (updated) {
            Toast.makeText(this, "Patient data updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update patient data", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Patient")
                .setMessage("Are you sure you want to delete this patient? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deletePatient())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePatient() {
        boolean deleted = db.deletePatient(patientId);
        if (deleted) {
            Toast.makeText(this, "Patient deleted successfully", Toast.LENGTH_SHORT).show();
            // Delete the associated X-ray image if it exists
            if (xrayImagePath != null && !xrayImagePath.isEmpty()) {
                File imageFile = new File(xrayImagePath);
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
            setResult(RESULT_OK);  // Set the result to OK
            finish();
        } else {
            Toast.makeText(this, "Failed to delete patient", Toast.LENGTH_SHORT).show();
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