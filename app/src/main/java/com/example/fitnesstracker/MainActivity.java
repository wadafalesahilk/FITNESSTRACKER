package com.example.fitnesstracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvStepsCount, tvDistance, tvCalories;
    private ProgressBar progressSteps;
    private DatabaseHelper databaseHelper;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isSensorPresent = false;

    private int initialStepCount = 0; // Tracks the initial step count from the sensor
    private int totalSteps = 0; // Tracks steps taken in the session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        tvStepsCount = findViewById(R.id.tvStepsCount);
        tvDistance = findViewById(R.id.tvDistance);
        tvCalories = findViewById(R.id.tvCalories);
        progressSteps = findViewById(R.id.progressSteps);
        Button btnViewHistory = findViewById(R.id.btnViewHistory);
        Button btnSettings = findViewById(R.id.btnSettings);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize Sensor Manager and Step Counter Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            Log.e("MainActivity", "Step Counter Sensor not available!");
            tvStepsCount.setText("Step Counter not available");
        }

        // Load previous total steps from the database
        loadData();

        // Button Actions
        btnViewHistory.setOnClickListener(v -> viewHistory());
        btnSettings.setOnClickListener(v -> openSettings());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int currentStepCount = (int) event.values[0];

            // Set the initial step count if it's the first sensor event
            if (initialStepCount == 0) {
                initialStepCount = currentStepCount; // Capture the initial value when the app starts
            }

            // Calculate total steps taken since the app started
            totalSteps = currentStepCount - initialStepCount;

            // Calculate distance and calories based on steps
            double distance = totalSteps * 0.0008; // Assuming average step length is 0.8 meters
            double calories = totalSteps * 0.04;   // Approximate calories per step

            // Update the database with the latest values
            databaseHelper.insertFitnessData(totalSteps, distance, calories);

            // Update the UI in real-time
            updateUI(totalSteps, distance, calories);
        }
    }

    private void updateUI(int steps, double distance, double calories) {
        tvStepsCount.setText(steps + " Steps");
        tvDistance.setText("Distance: " + String.format("%.2f km", distance));
        tvCalories.setText("Calories: " + String.format("%.2f kcal", calories));
        progressSteps.setProgress(Math.min(steps, 10000)); // Assuming a goal of 10,000 steps
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this implementation
    }

    // Load Data from Database
    private void loadData() {
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getLatestData();
            if (cursor != null && cursor.moveToFirst()) {
                int steps = cursor.getInt(cursor.getColumnIndexOrThrow("steps"));
                double distance = cursor.getDouble(cursor.getColumnIndexOrThrow("distance"));
                double calories = cursor.getDouble(cursor.getColumnIndexOrThrow("calories"));

                // Set the initial step count based on the last recorded steps
                initialStepCount = steps;

                Log.d("MainActivity", "Steps: " + steps);
                Log.d("MainActivity", "Distance: " + distance);
                Log.d("MainActivity", "Calories: " + calories);

                // Update UI with previous data
                updateUI(steps, distance, calories);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error loading data: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Open History Activity
    private void viewHistory() {
        Intent intent = new Intent(this, ActivityViewHistory.class);
        startActivity(intent);
    }

    // Open Settings Activity
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
