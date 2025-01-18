package com.example.fitnesstracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchStepTracking;
    private Switch switchNotifications;
    private SeekBar seekBarStepGoal;
    private TextView tvStepGoalValue;
    private Button btnResetData;

    private static final String PREFS_NAME = "FitnessTrackerPrefs";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        switchStepTracking = findViewById(R.id.switchStepTracking);
        switchNotifications = findViewById(R.id.switchNotifications);
        seekBarStepGoal = findViewById(R.id.seekBarStepGoal);
        tvStepGoalValue = findViewById(R.id.tvStepGoalValue);
        btnResetData = findViewById(R.id.btnResetData);

        // Load preferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadPreferences();

        // Set up listeners
        switchStepTracking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("stepTrackingEnabled", isChecked);
            editor.apply();
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationsEnabled", isChecked);
            editor.apply();
        });

        seekBarStepGoal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvStepGoalValue.setText(String.valueOf(progress));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("dailyStepGoal", progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnResetData.setOnClickListener(v -> resetData());
    }

    private void loadPreferences() {
        boolean stepTrackingEnabled = sharedPreferences.getBoolean("stepTrackingEnabled", true);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true);
        int dailyStepGoal = sharedPreferences.getInt("dailyStepGoal", 5000);

        switchStepTracking.setChecked(stepTrackingEnabled);
        switchNotifications.setChecked(notificationsEnabled);
        seekBarStepGoal.setProgress(dailyStepGoal);
        tvStepGoalValue.setText(String.valueOf(dailyStepGoal));
    }

    private void resetData() {
        DatabaseHelper db = new DatabaseHelper(this);
        db.deleteAllData(); // Add a method in DatabaseHelper to delete all data
        // Optionally, show a Toast message or a Snackbar to inform the user
    }
}
