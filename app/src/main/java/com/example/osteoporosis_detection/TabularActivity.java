package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.example.osteoporosis_detection.data.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class TabularActivity extends AppCompatActivity {

    private static final String TAG = "TabularActivity";
    private ListView listViewPatients;
    private Button buttonBack;
    private SearchView searchView;
    DatabaseHelper db;
    ArrayList<String> patientList;
    ArrayList<Integer> patientIds;
    ArrayList<String> originalPatientList;
    ArrayList<Integer> originalPatientIds;
    private ArrayAdapter<String> adapter;
    private ActivityResultLauncher<Intent> editPatientLauncher;

    private ImageView backIcon, menuIcon;
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabular);

        initializeUIComponents();
        setupToolbar();
        setupBottomNavigation();
        setupEditPatientLauncher();

        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        patientList = new ArrayList<>();
        patientIds = new ArrayList<>();
        originalPatientList = new ArrayList<>();
        originalPatientIds = new ArrayList<>();

        loadPatientData();

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(TabularActivity.this, MainActivity.class);
            startActivity(intent);
        });

        listViewPatients.setOnItemClickListener((parent, view, position, id) -> {
            try {
                if (position < patientIds.size()) {
                    int patientId = patientIds.get(position);
                    Intent intent = new Intent(TabularActivity.this, EditPatientActivity.class);
                    intent.putExtra("PATIENT_ID", patientId);
                    editPatientLauncher.launch(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error on item click: ", e);
                Toast.makeText(this, getString(R.string.error_opening_patient_details) + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        setupSearchView();
    }

    private void initializeUIComponents() {
        listViewPatients = findViewById(R.id.listViewPatients);
        buttonBack = findViewById(R.id.buttonBack);
        searchView = findViewById(R.id.searchView);
        backIcon = findViewById(R.id.backIcon);
        menuIcon = findViewById(R.id.menuIcon);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TabularActivity.this, StartingActivity.class);
            startActivity(intent);
            finish();
        });

        menuIcon.setOnClickListener(v -> showMenu());
    }

    private void showMenu() {
        PopupMenu popup = new PopupMenu(TabularActivity.this, menuIcon);
        popup.getMenuInflater().inflate(R.menu.header_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_settings) {
                startActivity(new Intent(TabularActivity.this, SettingsActivity.class));
                return true;
            } else if (itemId == R.id.menu_about) {
                startActivity(new Intent(TabularActivity.this, AboutActivity.class));
                return true;
            } else if (itemId == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(TabularActivity.this, StartingActivity.class));
                return true;
            } else if (itemId == R.id.navigation_registration) {
                startActivity(new Intent(TabularActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_prediction) {
                // We're already on the TabularActivity page
                return true;
            } else if (itemId == R.id.navigation_visualization) {
                startActivity(new Intent(TabularActivity.this, Visualisation.class));
                return true;
            } else if (itemId == R.id.navigation_doctors_profile) {
                startActivity(new Intent(TabularActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_prediction);
    }

    void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(TabularActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        refreshPatientData();
    }

    void loadPatientData() {
        Cursor cursor = null;
        try {
            cursor = db.getAllPatients();
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);

                if (idIndex == -1 || nameIndex == -1 || emailIndex == -1) {
                    throw new IllegalArgumentException(getString(R.string.missing_columns));
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
            Toast.makeText(this, getString(R.string.error_loading_patient_data) + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (patientList.isEmpty()) {
            Toast.makeText(this, R.string.no_patients_found, Toast.LENGTH_SHORT).show();
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

    void filterPatients(String query) {
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