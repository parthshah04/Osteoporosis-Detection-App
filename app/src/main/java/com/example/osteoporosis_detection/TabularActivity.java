
package com.example.osteoporosis_detection;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TabularActivity extends AppCompatActivity {

    private static final String TAG = "TabularActivity";
    private EditText editTextAge;
    private Spinner spinnerMedications, spinnerHormonalChanges, spinnerFamilyHistory, spinnerBodyWeight, spinnerCalciumIntake,
            spinnerVitaminDIntake, spinnerPhysicalActivity, spinnerSmoking, spinnerAlcoholConsumption,
            spinnerMedicalConditions, spinnerPriorFractures;
    private Button buttonPredict;
    private TextView textViewResult;
    private Interpreter tfliteTabular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabular);

        // Initialize UI components
        editTextAge = findViewById(R.id.editTextAge);
        spinnerMedications = findViewById(R.id.spinnerMedications);
        spinnerHormonalChanges = findViewById(R.id.spinnerHormonalChanges);
        //spinnerFamilyHistory = findViewById(R.id.spinnerFamilyHistory);
        spinnerBodyWeight = findViewById(R.id.spinnerBodyWeight);
        spinnerCalciumIntake = findViewById(R.id.spinnerCalciumIntake);
        spinnerVitaminDIntake = findViewById(R.id.spinnerVitaminDIntake);
        spinnerPhysicalActivity = findViewById(R.id.spinnerPhysicalActivity);
        spinnerSmoking = findViewById(R.id.spinnerSmoking);
        spinnerAlcoholConsumption = findViewById(R.id.spinnerAlcoholConsumption);
        spinnerMedicalConditions = findViewById(R.id.spinnerMedicalConditions);
        spinnerPriorFractures = findViewById(R.id.spinnerPriorFractures);
        buttonPredict = findViewById(R.id.buttonPredict);
        textViewResult = findViewById(R.id.textViewResult);

        // Load model
        try {
            tfliteTabular = new Interpreter(loadModelFile("Tabular_Model.tflite"));
            Log.d(TAG, "Tabular model loaded successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Error loading tabular model: ", e);
        }

        // Set up listeners
        buttonPredict.setOnClickListener(v -> makePrediction());
    }

    private MappedByteBuffer loadModelFile(String modelFileName) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(getAssets().openFd(modelFileName).getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = getAssets().openFd(modelFileName).getStartOffset();
            long declaredLength = getAssets().openFd(modelFileName).getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private void makePrediction() {
        try {
            // Check if models are loaded
            if (tfliteTabular == null) {
                Log.e(TAG, "Tabular model not loaded.");
                textViewResult.setText("Error: Tabular model not loaded.");
                return;
            }

            // Extract and preprocess input data
            float[] inputFeatures = new float[12];
            if (!editTextAge.getText().toString().isEmpty()) {
                inputFeatures[0] = Float.parseFloat(editTextAge.getText().toString());
            } else {
                textViewResult.setText("Please enter age.");
                return;
            }
            inputFeatures[1] = spinnerBodyWeight.getSelectedItemPosition();
            inputFeatures[2] = spinnerSmoking.getSelectedItemPosition();
            inputFeatures[3] = spinnerMedications.getSelectedItemPosition();
            inputFeatures[4] = spinnerHormonalChanges.getSelectedItemPosition();
            inputFeatures[5] = spinnerFamilyHistory.getSelectedItemPosition();
            inputFeatures[6] = spinnerCalciumIntake.getSelectedItemPosition();
            inputFeatures[7] = spinnerVitaminDIntake.getSelectedItemPosition();
            inputFeatures[8] = spinnerPhysicalActivity.getSelectedItemPosition();
            inputFeatures[9] = spinnerAlcoholConsumption.getSelectedItemPosition();
            inputFeatures[10] = spinnerMedicalConditions.getSelectedItemPosition();
            inputFeatures[11] = spinnerPriorFractures.getSelectedItemPosition();

            // Perform inference using tabular model
            float[][] inputBuffer = new float[1][12];
            inputBuffer[0] = inputFeatures;
            float[][] outputBuffer = new float[1][1];
            tfliteTabular.run(inputBuffer, outputBuffer);

            float tabularPrediction = outputBuffer[0][0];
            Log.d(TAG, "Tabular prediction: " + tabularPrediction);

            // Display the prediction
            String resultText = "The likelihood of osteoporosis based on your symptoms is: " + (tabularPrediction * 100) + "%";
            textViewResult.setText(resultText);
        } catch (Exception e) {
            Log.e(TAG, "Error making prediction: ", e);
            textViewResult.setText("Error in prediction.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tfliteTabular != null) {
            tfliteTabular.close();
        }
    }
}