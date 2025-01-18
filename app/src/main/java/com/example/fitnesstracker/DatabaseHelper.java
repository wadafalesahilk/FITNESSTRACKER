package com.example.fitnesstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FitnessTrackerDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "FitnessData";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STEPS = "steps";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_STEPS + " INTEGER NOT NULL, "
                + COLUMN_DISTANCE + " DOUBLE NOT NULL, "
                + COLUMN_CALORIES + " DOUBLE NOT NULL, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createTableQuery);
        Log.d("DatabaseHelper", "Database table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Log.d("DatabaseHelper", "Database upgraded");
    }

    /**
     * Insert fitness data into the database.
     *
     * @param steps    Number of steps.
     * @param distance Distance covered in kilometers.
     * @param calories Calories burned.
     */
    public void insertFitnessData(int steps, double distance, double calories) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_STEPS, steps);
            values.put(COLUMN_DISTANCE, distance);
            values.put(COLUMN_CALORIES, calories);

            Log.d("DatabaseHelper", "Inserting Steps: " + steps + ", Distance: " + distance + ", Calories: " + calories);

            long newRowId = db.insert(TABLE_NAME, null, values);
            if (newRowId == -1) {
                Log.e("DatabaseHelper", "Error inserting data");
            } else {
                Log.d("DatabaseHelper", "Data inserted with ID: " + newRowId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting data: " + e.getMessage(), e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Get the latest fitness data (most recent entry).
     *
     * @return Cursor containing the latest data.
     */
    public Cursor getLatestData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";
        return db.rawQuery(query, null);
    }

    /**
     * Get today's fitness data.
     *
     * @return Cursor containing today's data.
     */
    public Cursor getTodayData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE DATE(" + COLUMN_TIMESTAMP + ") = DATE('now')";
        return db.rawQuery(query, null);
    }

    /**
     * Get this week's fitness data (last 7 days including today).
     *
     * @return Cursor containing this week's data.
     */
    public Cursor getWeekData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE DATE(" + COLUMN_TIMESTAMP + ") >= DATE('now', '-6 days')";
        return db.rawQuery(query, null);
    }

    /**
     * Get this month's fitness data (last 30 days including today).
     *
     * @return Cursor containing this month's data.
     */
    public Cursor getMonthData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE DATE(" + COLUMN_TIMESTAMP + ") >= DATE('now', '-30 days')";
        return db.rawQuery(query, null);
    }

    /**
     * Close the database safely.
     *
     * @param cursor Cursor object to be closed.
     */
    public void closeDatabase(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
    /**
     * Delete all fitness data from the database.
     */
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        Log.d("DatabaseHelper", "All data deleted");
    }

}
