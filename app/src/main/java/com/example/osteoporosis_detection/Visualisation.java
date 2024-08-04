package com.example.osteoporosis_detection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.example.osteoporosis_detection.data.DatabaseHelper;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Visualisation extends AppCompatActivity {

    private static final String TAG = "Visualisation";
    private DatabaseHelper db;
    private TextView totalPatientsTextView;
    private HorizontalBarChart ageGroupChart;
    private PieChart osteoporosisPieChart;
    private ImageView backIcon, menuIcon;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualisation);

        try {
            db = new DatabaseHelper(this);
            sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

            totalPatientsTextView = findViewById(R.id.totalPatientsTextView);
            ageGroupChart = findViewById(R.id.ageGroupChart);
            osteoporosisPieChart = findViewById(R.id.osteoporosisPieChart);
            backIcon = findViewById(R.id.backIcon);
            menuIcon = findViewById(R.id.menuIcon);
            bottomNavigationView = findViewById(R.id.bottomNavigationView);

            // Set up toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            setupBackIcon();
            setupMenuIcon();
            setupBottomNavigation();

            displayTotalPatients();
            displayAgeGroupDistribution();
            displayOsteoporosisDistribution();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            showErrorMessage(getString(R.string.error_visualisation_message));
        }
    }

    private void setupBackIcon() {
        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Visualisation.this, StartingActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupMenuIcon() {
        menuIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(Visualisation.this, menuIcon);
            popup.getMenuInflater().inflate(R.menu.header_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_settings) {
                    startActivity(new Intent(Visualisation.this, SettingsActivity.class));
                    return true;
                } else if (itemId == R.id.menu_about) {
                    startActivity(new Intent(Visualisation.this, AboutActivity.class));
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    logout();
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(Visualisation.this, StartingActivity.class));
                return true;
            } else if (itemId == R.id.navigation_registration) {
                startActivity(new Intent(Visualisation.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_prediction) {
                startActivity(new Intent(Visualisation.this, TabularActivity.class));
                return true;
            } else if (itemId == R.id.navigation_visualization) {
                // We're already on the Visualisation page
                return true;
            } else if (itemId == R.id.navigation_doctors_profile) {
                startActivity(new Intent(Visualisation.this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Set the visualization item as selected
        bottomNavigationView.setSelectedItemId(R.id.navigation_visualization);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(Visualisation.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void displayTotalPatients() {
        try {
            int totalPatients = db.getTotalPatients();
            totalPatientsTextView.setText(getString(R.string.total_patients) + totalPatients);
        } catch (Exception e) {
            Log.e(TAG, "Error in displayTotalPatients: ", e);
            showErrorMessage(getString(R.string.failed_to_display_total_patients));
        }
    }

    private void displayAgeGroupDistribution() {
        try {
            Map<String, Integer> ageGroupCounts = db.getAgeGroupDistribution();
            List<BarEntry> entries = new ArrayList<>();
            String[] ageGroups = {"0-20", "21-40", "41-60", "61-80", "80+"};

            for (int i = 0; i < ageGroups.length; i++) {
                int count = ageGroupCounts.getOrDefault(ageGroups[i], 0);
                entries.add(new BarEntry(i, count));
            }

            BarDataSet dataSet = new BarDataSet(entries, getString(R.string.age_groups));
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            BarData barData = new BarData(dataSet);
            barData.setValueTextSize(12f);
            barData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }
            });
            ageGroupChart.setData(barData);

            // Y-Axis
            XAxis xAxis = ageGroupChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(ageGroups));

            // X Axis
            YAxis leftAxis = ageGroupChart.getAxisLeft();
            leftAxis.setDrawGridLines(false);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }
            });
            leftAxis.setDrawLabels(false);
            leftAxis.setDrawAxisLine(false);
            // X-Axis
            YAxis rightAxis = ageGroupChart.getAxisRight();
            rightAxis.setEnabled(false);  // This removes the numbers from the top

            ageGroupChart.setFitBars(true);
            ageGroupChart.getDescription().setEnabled(false);
            ageGroupChart.getLegend().setEnabled(false);  // Disable legend if not needed
            ageGroupChart.setDrawValueAboveBar(true);
            ageGroupChart.animateY(1000);

            // Adjust the top padding to remove extra space
            ageGroupChart.setExtraTopOffset(-30f);

            ageGroupChart.invalidate();

            // Set chart title
            TextView ageChartTitle = findViewById(R.id.ageChartTitle);
            ageChartTitle.setText(R.string.age_group_distribution);
            ageChartTitle.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "Error in displayAgeGroupDistribution: ", e);
            showErrorMessage(getString(R.string.failed_to_display_age_group_distribution));
        }
    }

    private void displayOsteoporosisDistribution() {
        try {
            float threshold = 0.75f; // 75% threshold
            int[] counts = db.getOsteoporosisCountBasedOnAverage(threshold);

            if (counts[0] == 0 && counts[1] == 0) {
                showErrorMessage(getString(R.string.no_data_available_for_osteoporosis_distribution));
                return;
            }

            List<PieEntry> entries = new ArrayList<>();
            if (counts[0] > 0) {
                entries.add(new PieEntry(counts[0], getString(R.string.without_osteoporosis)));
            }
            if (counts[1] > 0) {
                entries.add(new PieEntry(counts[1], getString(R.string.with_osteoporosis)));
            }

            PieDataSet dataSet = new PieDataSet(entries, getString(R.string.osteoporosis_distribution));
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextSize(14f);

            dataSet.setValueFormatter(new PercentFormatter(osteoporosisPieChart));

            PieData pieData = new PieData(dataSet);
            osteoporosisPieChart.setData(pieData);
            osteoporosisPieChart.setUsePercentValues(true);
            osteoporosisPieChart.getDescription().setEnabled(false);
            osteoporosisPieChart.setEntryLabelTextSize(14f);
            osteoporosisPieChart.setCenterText(getString(R.string.osteoporosis_distribution_space));
            osteoporosisPieChart.setCenterTextSize(18f);
            osteoporosisPieChart.setHoleRadius(40f);
            osteoporosisPieChart.setTransparentCircleRadius(45f);
            osteoporosisPieChart.setDrawEntryLabels(true);
            osteoporosisPieChart.getLegend().setEnabled(true);
            osteoporosisPieChart.invalidate();

            // Set chart title
            TextView pieChartTitle = findViewById(R.id.pieChartTitle);
            pieChartTitle.setText(R.string.osteoporosis_distribution);
            pieChartTitle.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e(TAG, "Error in displayOsteoporosisDistribution: " + e.getMessage(), e);
            showErrorMessage(getString(R.string.failed_to_display_osteoporosis_distribution));
        }
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        TextView noDataText = findViewById(R.id.noDataText);
        if (noDataText != null) {
            noDataText.setText(message);
            noDataText.setVisibility(View.VISIBLE);
        }
    }
}