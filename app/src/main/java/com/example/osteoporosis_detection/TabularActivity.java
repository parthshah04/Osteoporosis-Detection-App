package com.example.osteoporosis_detection;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.osteoporosis_detection.data.DatabaseHelper;

import java.util.ArrayList;

public class TabularActivity extends AppCompatActivity {

    private static final String TAG = "TabularActivity";
    private ListView listViewPatients;
    private Button buttonBack;
    private SearchView searchView;
    private DatabaseHelper db;
    private ArrayList<String> patientList;
    private ArrayList<Integer> patientIds;
    private ArrayList<String> originalPatientList;
    private ArrayList<Integer> originalPatientIds;
    private ArrayAdapter<String> adapter;
    private ActivityResultLauncher<Intent> editPatientLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabular);

        listViewPatients = findViewById(R.id.listViewPatients);
        buttonBack = findViewById(R.id.buttonBack);
        searchView = findViewById(R.id.searchView);
        db = new DatabaseHelper(this);
        patientList = new ArrayList<>();
        patientIds = new ArrayList<>();
        originalPatientList = new ArrayList<>();
        originalPatientIds = new ArrayList<>();

        loadPatientData();

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(TabularActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        listViewPatients.setOnItemClickListener((parent, view, position, id) -> {
            try {
                if (position < patientIds.size()) {
                    int patientId = patientIds.get(position);
                    Intent intent = new Intent(TabularActivity.this, EditPatientActivity.class);
                    intent.putExtra("PATIENT_ID", patientId);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error on item click: ", e);
                Toast.makeText(this, "Error opening patient details: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        setupSearchView();
    }

    private void setupEditPatientLauncher() {
        editPatientLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshPatientData();
                    }
                }
        );
    }

    private void refreshPatientData() {
        patientList.clear();
        patientIds.clear();
        originalPatientList.clear();
        originalPatientIds.clear();
        loadPatientData();
        adapter.notifyDataSetChanged();
    }

    protected void onResume() {
        super.onResume();
        refreshPatientData();
    }
    private void loadPatientData() {
        Cursor cursor = null;
        try {
            cursor = db.getAllPatients();
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);

                if (idIndex == -1 || nameIndex == -1 || emailIndex == -1) {
                    throw new IllegalArgumentException("One or more required columns are missing from the cursor.");
                }

                do {
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String email = cursor.getString(emailIndex);
                    patientList.add(name + " - " + email);
                    patientIds.add(id);
                    originalPatientList.add(name + " - " + email);
                    originalPatientIds.add(id);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading patient data: ", e);
            Toast.makeText(this, "Error loading patient data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (patientList.isEmpty()) {
            Toast.makeText(this, "No patients found.", Toast.LENGTH_SHORT).show();
        }

        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, patientList);
            listViewPatients.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPatients(newText);
                return true;
            }
        });
    }

    private void filterPatients(String query) {
        ArrayList<String> filteredList = new ArrayList<>();
        ArrayList<Integer> filteredIds = new ArrayList<>();

        for (int i = 0; i < originalPatientList.size(); i++) {
            if (originalPatientList.get(i).toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(originalPatientList.get(i));
                filteredIds.add(originalPatientIds.get(i));
            }
        }

        runOnUiThread(() -> {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredList);
            listViewPatients.setAdapter(adapter);
            patientIds = filteredIds;
        });
    }
}