package com.example.callhistory.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CallLogDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "call_logs.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CALL_LOGS = "call_logs";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_NUMBER = "number";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_CALL_TYPE = "call_type";

    public CallLogDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public boolean isCallLogEntryExists(CallLogItem item) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CALL_LOGS, new String[]{COLUMN_ID},
                COLUMN_NUMBER + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{item.getNumber(), item.getDate()}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void addCallLogEntry(CallLogItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_NUMBER, item.getNumber());
        values.put(COLUMN_DATE, item.getDate());
        values.put(COLUMN_TIME, item.getTime());
        values.put(COLUMN_CALL_TYPE, item.getCallType());
        db.insert(TABLE_CALL_LOGS, null, values);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table when the database is created
        String createTableQuery = "CREATE TABLE " + TABLE_CALL_LOGS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_NUMBER + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_CALL_TYPE + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    // ... (Other methods as needed)
}
