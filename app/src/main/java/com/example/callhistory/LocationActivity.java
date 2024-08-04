package com.example.callhistory;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class LocationActivity extends AppCompatActivity {
    private static final int REQUEST_CALL_LOG_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private TextView callLogTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        callLogTextView = findViewById(R.id.callLogTextView);

        // Request permission to access call logs
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_CALL_LOG_PERMISSION);
        } else {
            // Permission already granted, fetch call logs
            fetchCallLogs();
        }

        // Request permission to access location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, fetch location
            fetchLocation();
        }
    }

    // Handle permission requests result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_LOG_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch call logs
                fetchCallLogs();
            } else {
                Toast.makeText(this, "Call Log permission is required to retrieve call data.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch location
                fetchLocation();
            } else {
                Toast.makeText(this, "Location permission is required to retrieve location data.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Fetch recent call logs
    private void fetchCallLogs() {
        // Define the projection to retrieve the desired columns from the call logs
        String[] projection = {CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME};

        // Query the call logs to retrieve the recent 5 calls
        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                CallLog.Calls.DATE + " DESC LIMIT 5"
        );

        StringBuilder callLogText = new StringBuilder();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                @SuppressLint("Range") String callerName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

                callLogText.append("Name: ").append(callerName).append("\n");
                callLogText.append("Number: ").append(phoneNumber).append("\n");

                // Fetch and append location information
                String location = fetchLocation();
                callLogText.append("Location: ").append(location).append("\n\n");
            }
            cursor.close();
        }

        callLogTextView.setText(callLogText.toString());
    }

    // Fetch location information
    private String fetchLocation() {
        String locationInfo = "Location not available";

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get location manager
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                locationInfo = "Latitude: " + latitude + ", Longitude: " + longitude;
            }
        }

        return locationInfo;
    }
}
