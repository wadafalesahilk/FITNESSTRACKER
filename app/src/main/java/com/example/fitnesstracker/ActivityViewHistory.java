package com.example.fitnesstracker;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActivityViewHistory extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyItemList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        // Initialize views
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        TextView tvToday = findViewById(R.id.tvToday);
        TextView tvThisWeek = findViewById(R.id.tvThisWeek);
        TextView tvThisMonth = findViewById(R.id.tvThisMonth);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Set up RecyclerView
        historyItemList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyItemList);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistory.setAdapter(historyAdapter);

        // Load initial data from the database
        loadData("ALL");

        // Set up button click listeners for date filters
        tvToday.setOnClickListener(v -> loadData("TODAY"));
        tvThisWeek.setOnClickListener(v -> loadData("WEEK"));
        tvThisMonth.setOnClickListener(v -> loadData("MONTH"));
    }

    private void loadData(String filter) {
        // Clear the existing list
        historyItemList.clear();

        Cursor cursor;
        switch (filter) {
            case "TODAY":
                cursor = databaseHelper.getTodayData();
                break;
            case "WEEK":
                cursor = databaseHelper.getWeekData();
                break;
            case "MONTH":
                cursor = databaseHelper.getMonthData();
                break;
            default:
                cursor = databaseHelper.getLatestData(); // Load all data
                break;
        }

        // Parse data from the cursor
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                int steps = cursor.getInt(cursor.getColumnIndexOrThrow("steps"));
                double distance = cursor.getDouble(cursor.getColumnIndexOrThrow("distance"));
                double calories = cursor.getDouble(cursor.getColumnIndexOrThrow("calories"));

                // Add data to the list
                historyItemList.add(new HistoryItem(date, steps, distance, calories));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "No data found for the selected filter", Toast.LENGTH_SHORT).show();
        }

        // Notify adapter to update the RecyclerView
        historyAdapter.notifyDataSetChanged();
    }

    // Define a model class for history items
    public static class HistoryItem {
        private String date;
        private int steps;
        private double distance; // in km
        private double calories; // in kcal

        public HistoryItem(String date, int steps, double distance, double calories) {
            this.date = date;
            this.steps = steps;
            this.distance = distance;
            this.calories = calories;
        }

        public String getDate() {
            return date;
        }

        public int getSteps() {
            return steps;
        }

        public double getDistance() {
            return distance;
        }

        public double getCalories() {
            return calories;
        }
    }
}
