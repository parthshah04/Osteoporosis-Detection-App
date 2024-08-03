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

import com.example.osteoporosis_detection.data.DatabaseHelper;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "MainActivity";

    private EditText editTextAge, editTextName, editTextEmail;
    private Spinner spinnerMedications, spinnerHormonalChanges, spinnerFamilyHistory, spinnerBodyWeight, spinnerCalciumIntake,
            spinnerVitaminDIntake, spinnerPhysicalActivity, spinnerSmoking, spinnerAlcoholConsumption,
            spinnerMedicalConditions, spinnerPriorFractures;
    private ImageView imageView;
    private Button buttonSelectImage, buttonPredict, buttonSave;
    private TextView textViewResult, textViewImagePrediction, textViewTabularPrediction;
    private ProgressBar progressBarResult;
    private Bitmap xRayImage;
    private Interpreter tfliteVGG19;
    private Interpreter tfliteTabular;
    private DatabaseHelper db;
    private float finalConfidenceScore;
    private String imagePredictionText;
    private String tabularPredictionText;
    private boolean predictionMade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUIComponents();
        db = new DatabaseHelper(this);
        setupListeners();
        loadModels();
    }

    private void initializeUIComponents() {
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
    }

    private void setupListeners() {
        buttonSelectImage.setOnClickListener(v -> selectImage());
        buttonPredict.setOnClickListener(v -> makePrediction());
        buttonSave.setOnClickListener(v -> saveData());
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
            if (tfliteTabular == null || tfliteVGG19 == null) {
                Log.e(TAG, "Models not loaded.");
                textViewResult.setText(R.string.error_models_not_loaded);
                return;
            }

            if (editTextAge.getText().toString().isEmpty()) {
                textViewResult.setText(R.string.please_select_an_image);
                return;
            }

            float[] tabularInput = extractTabularInput();
            float tabularPrediction = predictTabularModel(tabularInput);
            Log.d(TAG, "Tabular prediction: " + tabularPrediction);

            if (xRayImage == null) {
                Log.e(TAG, "No image selected.");
                textViewResult.setText(R.string.please_select_an_image);
                return;
            }
            float imagePrediction = predictImageModel(xRayImage);
            Log.d(TAG, "Image prediction: " + imagePrediction);

            finalConfidenceScore = (tabularPrediction + imagePrediction) / 2;
            String resultText = getString(R.string.prediction_desc) + String.format("%.2f", finalConfidenceScore * 100) + "%";
            textViewResult.setText(resultText);

            imagePredictionText = getString(R.string.prediction_of_image_data) + (imagePrediction > 0.5 ? getString(R.string.osteoporosis) : " Normal");
            tabularPredictionText = getString(R.string.prediction_of_tabular_data) + (tabularPrediction > 0.5 ? getString(R.string.osteoporosis) : " Normal");
            textViewImagePrediction.setText(imagePredictionText);
            textViewTabularPrediction.setText(tabularPredictionText);

            progressBarResult.setProgress((int) (finalConfidenceScore * 100));

            predictionMade = true;

        } catch (Exception e) {
            Log.e(TAG, "Error making prediction: ", e);
            textViewResult.setText(R.string.please_fill_in_all_fields);
            predictionMade = false;
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

    private float predictImageModel(Bitmap bitmap) {
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
    }

    private void saveData() {
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String age = editTextAge.getText().toString();
        String xrayImagePath = null;
        if (xRayImage != null) {
            xrayImagePath = saveImageToInternalStorage(xRayImage);
        }

        if (name.isEmpty() || email.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, R.string.please_fill_in_all_fields, Toast.LENGTH_SHORT).show();
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

        String result = predictionMade ? textViewResult.getText().toString() : "";
        String tabularPredictionValue = predictionMade ? this.tabularPredictionText : "";
        String imagePredictionValue = predictionMade ? this.imagePredictionText : "";
        float finalConfidenceScore = predictionMade ? this.finalConfidenceScore : 0f;


        db.insertPredictionData(name, email, age, tabularPredictionValue, imagePredictionValue, result, medications, hormonalChanges, familyHistory,
                bodyWeight, calciumIntake, vitaminDIntake, physicalActivity, smoking, alcoholConsumption, medicalConditions, priorFractures, xrayImagePath, finalConfidenceScore, predictionMade);
        Log.d(TAG, "Data saved. Prediction made: " + predictionMade);
        Toast.makeText(this, R.string.data_saved_successfully, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, TabularActivity.class);
        startActivity(intent);
        clearInputFields();
    }

    private void clearInputFields() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextAge.setText("");
        spinnerMedications.setSelection(0);
        spinnerHormonalChanges.setSelection(0);
        spinnerFamilyHistory.setSelection(0);
        spinnerBodyWeight.setSelection(0);
        spinnerCalciumIntake.setSelection(0);
        spinnerVitaminDIntake.setSelection(0);
        spinnerPhysicalActivity.setSelection(0);
        spinnerSmoking.setSelection(0);
        spinnerAlcoholConsumption.setSelection(0);
        spinnerMedicalConditions.setSelection(0);
        spinnerPriorFractures.setSelection(0);
        imageView.setImageResource(R.drawable.about);
        xRayImage = null;
        textViewResult.setText("");
        textViewImagePrediction.setText("");
        textViewTabularPrediction.setText("");
        progressBarResult.setProgress(0);
        predictionMade = false;
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
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