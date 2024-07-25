package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.example.osteoporosis_detection.data.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "MainActivity";

    private EditText editTextAge, editTextName, editTextEmail;
    private Spinner spinnerMedications, spinnerHormonalChanges, spinnerFamilyHistory, spinnerBodyWeight, spinnerCalciumIntake,
            spinnerVitaminDIntake, spinnerPhysicalActivity, spinnerSmoking, spinnerAlcoholConsumption,
            spinnerMedicalConditions, spinnerPriorFractures;
    private ImageView imageView;
    private Button buttonSelectImage, buttonPredict, buttonSave, buttonViewPatients;
    private TextView textViewResult, textViewImagePrediction, textViewTabularPrediction;
    private ProgressBar progressBarResult;
    private Bitmap xRayImage;
    private Interpreter tfliteVGG19;
    private Interpreter tfliteTabular;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        editTextAge = findViewById(R.id.editTextAge);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
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
        imageView = findViewById(R.id.imageView);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonPredict = findViewById(R.id.buttonPredict);
        buttonSave = findViewById(R.id.buttonSave);
        textViewResult = findViewById(R.id.textViewResult);
        textViewImagePrediction = findViewById(R.id.textViewImagePrediction);
        textViewTabularPrediction = findViewById(R.id.textViewTabularPrediction);
        progressBarResult = findViewById(R.id.progressBarResult);

        // Initialize database helper
        db = new DatabaseHelper(this);

        // Set up listeners
        buttonSelectImage.setOnClickListener(v -> selectImage());
        buttonPredict.setOnClickListener(v -> makePrediction());
        buttonSave.setOnClickListener(v -> saveData());

        // Load models
        try {
            tfliteVGG19 = new Interpreter(loadModelFile("vgg19_finetuned_best_quantized.tflite"));
            tfliteTabular = new Interpreter(loadModelFile("Tabular_Model.tflite"));
            Log.d(TAG, "Models loaded successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Error loading models: ", e);
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                xRayImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(xRayImage);
                Log.d(TAG, "Image selected successfully.");
            } catch (IOException e) {
                Log.e(TAG, "Error getting image: ", e);
            }
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
            float tabularPrediction = predictTabularModel(tabularInput);
            Log.d(TAG, "Tabular prediction: " + tabularPrediction);

            // Perform inference using VGG-19 model
            if (xRayImage == null) {
                Log.e(TAG, "No image selected.");
                textViewResult.setText("Please select an image.");
                return;
            }
            float imagePrediction = predictImageModel(xRayImage);
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
            textViewResult.setText("Error in prediction.");
        }
    }

    private float predictTabularModel(float[] inputFeatures) {
        float[][] inputBuffer = new float[1][12];
        inputBuffer[0] = inputFeatures;
        float[][] outputBuffer = new float[1][1];
        tfliteTabular.run(inputBuffer, outputBuffer);
        return outputBuffer[0][0];
    }

    private float predictImageModel(Bitmap bitmap) {
        // Preprocess the image manually
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

        // Prepare input buffer and output buffer
        float[][] outputBuffer = new float[1][2]; // Adjusted for the shape returned by the VGG-19 model
        tfliteVGG19.run(byteBuffer, outputBuffer);
        return outputBuffer[0][1]; // Assuming the positive class (osteoporosis) is at index 1
    }

    private void saveData() {
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String age = editTextAge.getText().toString();
        String tabularPrediction = textViewTabularPrediction.getText().toString();
        String imagePrediction = textViewImagePrediction.getText().toString();
        String result = textViewResult.getText().toString();
        String xrayImagePath = null;
        if (xRayImage != null) {
            xrayImagePath = saveImageToInternalStorage(xRayImage);
        }


        if (name.isEmpty() || email.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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

        // Save data to database
        db.insertPredictionData(name, email, age, tabularPrediction, imagePrediction, result, medications, hormonalChanges, familyHistory,
                bodyWeight, calciumIntake, vitaminDIntake, physicalActivity, smoking, alcoholConsumption, medicalConditions, priorFractures, xrayImagePath);
        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
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