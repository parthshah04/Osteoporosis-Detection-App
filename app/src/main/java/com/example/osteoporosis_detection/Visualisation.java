package com.example.osteoporosis_detection;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Visualisation extends AppCompatActivity {

    private static final String TAG = "Visualisation";
    private DatabaseHelper db;
    private TextView totalPatientsTextView;
    private HorizontalBarChart ageGroupChart;
    private PieChart osteoporosisPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualisation);

        try {
            db = new DatabaseHelper(this);

            totalPatientsTextView = findViewById(R.id.totalPatientsTextView);
            ageGroupChart = findViewById(R.id.ageGroupChart);
            osteoporosisPieChart = findViewById(R.id.osteoporosisPieChart);

            displayTotalPatients();
            displayAgeGroupDistribution();
            displayOsteoporosisDistribution();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            showErrorMessage("An error occurred while initializing the visualization.");
        }
    }

    private void displayTotalPatients() {
        try {
            int totalPatients = db.getTotalPatients();
            totalPatientsTextView.setText("Total Patients: " + totalPatients);
        } catch (Exception e) {
            Log.e(TAG, "Error in displayTotalPatients: ", e);
            showErrorMessage("Failed to display total patients.");
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

            BarDataSet dataSet = new BarDataSet(entries, "Age Groups");
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
            ageChartTitle.setText("Age Group Distribution");
            ageChartTitle.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "Error in displayAgeGroupDistribution: ", e);
            showErrorMessage("Failed to display age group distribution.");
        }
    }

    private void displayOsteoporosisDistribution() {
        try {
            float threshold = 0.75f; // 75% threshold
            int[] counts = db.getOsteoporosisCountBasedOnAverage(threshold);

            if (counts[0] == 0 && counts[1] == 0) {
                showErrorMessage("No data available for osteoporosis distribution.");
                return;
            }

            List<PieEntry> entries = new ArrayList<>();
            if (counts[0] > 0) {
                entries.add(new PieEntry(counts[0], "Without Osteoporosis"));
            }
            if (counts[1] > 0) {
                entries.add(new PieEntry(counts[1], "With Osteoporosis"));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Osteoporosis Distribution");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextSize(14f);

            dataSet.setValueFormatter(new PercentFormatter(osteoporosisPieChart));

            PieData pieData = new PieData(dataSet);
            osteoporosisPieChart.setData(pieData);
            osteoporosisPieChart.setUsePercentValues(true);
            osteoporosisPieChart.getDescription().setEnabled(false);
            osteoporosisPieChart.setEntryLabelTextSize(14f);
            osteoporosisPieChart.setCenterText("Osteoporosis\nDistribution");
            osteoporosisPieChart.setCenterTextSize(18f);
            osteoporosisPieChart.setHoleRadius(40f);
            osteoporosisPieChart.setTransparentCircleRadius(45f);
            osteoporosisPieChart.setDrawEntryLabels(true);
            osteoporosisPieChart.getLegend().setEnabled(true);
            osteoporosisPieChart.invalidate();

            // Set chart title
            TextView pieChartTitle = findViewById(R.id.pieChartTitle);
            pieChartTitle.setText("Osteoporosis Distribution");
            pieChartTitle.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e(TAG, "Error in displayOsteoporosisDistribution: " + e.getMessage(), e);
            showErrorMessage("Failed to display osteoporosis distribution.");
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