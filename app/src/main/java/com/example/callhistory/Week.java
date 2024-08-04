package com.example.callhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Week extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private CardView cardView,cardView2;
    private LinearLayout expandLayout,expandLayout2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);
        cardView = findViewById(R.id.overall_cardview);
        expandLayout = findViewById(R.id.hiddenLayout);
        cardView2 = findViewById(R.id.overall_cardview2);
        expandLayout2 = findViewById(R.id.hiddenLayout2);
        readCallLogDataForLast7Days();


        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int menuItemId = item.getItemId();

                if (menuItemId == R.id.dashboard) {

                    progressDialog.setMessage("Loading Dialer..."); // Set your message
                    progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside


// Show the ProgressDialog
                    progressDialog.show();


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Start the next activity
                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
                            overridePendingTransition(0, 0);
                        }
                    }, 1000);
                    return true;
                } else if (menuItemId == R.id.home) {
                    return true;
                } /*else if (menuItemId == R.id.about) {
                    startActivity(new Intent(getApplicationContext(), Share.class));
                    overridePendingTransition(0, 0);

                    return true;
                }*/

                return false;
            }

        });



        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the current visibility of the LinearLayout
                if (expandLayout2.getVisibility() == View.GONE) {
                    // If it's gone, make it visible
                    expandLayout2.setVisibility(View.VISIBLE);
                } else {
                    // If it's visible, make it gone
                    expandLayout2.setVisibility(View.GONE);
                }
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the current visibility of the LinearLayout
                if (expandLayout.getVisibility() == View.GONE) {
                    // If it's gone, make it visible
                    expandLayout.setVisibility(View.VISIBLE);
                } else {
                    // If it's visible, make it gone
                    expandLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.filter_menu, popupMenu.getMenu());
        final int MENU_OPTION_1_ID = R.id.menu_option_1;
        final int MENU_OPTION_2_ID = R.id.menu_option_2;
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                long startTimeMillis;
                if (item.getItemId() == R.id.menu_option_1) {
                    // Create an Intent to open TodayActivity
                    startActivity(new Intent(Week.this,TodayActivity.class));
                    finish();

                    return true;
                } else if (item.getItemId() == R.id.menu_option_2) {
                    startActivity(new Intent(Week.this,Yesterday.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_3) {
                    startActivity(new Intent(Week.this,Week.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_4) {
                    startActivity(new Intent(Week.this,month.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_5) {
                    startActivity(new Intent(Week.this,year.class));
                    finish();
                    return true;
                }else if (item.getItemId() == R.id.menu_option_6) {
                    startActivity(new Intent(Week.this,MainActivity.class));
                    finish();
                    return true;
                } else {


                    return false;
                }
            }
        });

        popupMenu.show();

    }
    private void readCallLogDataForLast7Days() {
        // Initialize LineChart view
        LineChart lineChart = findViewById(R.id.lineChart);

        // Create an ArrayList to store dates for the past 7 days
        ArrayList<String> dates = new ArrayList<>();

        // Populate the dates ArrayList with date strings for each day
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            dates.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, -1); // Subtract one day from the current date
        }

        // Reverse the dates list to display them in the correct order (from oldest to newest)
        Collections.reverse(dates);

        // Create an ArrayList to store call data for the past 7 days
        ArrayList<Entry> entries = new ArrayList<>();

        int[] totalCallsArray = new int[7]; // Added to store total calls for each day

        // Iterate over the last 7 days
        for (int day = 0; day < 7; day++) {
            // Calculate the start and end timestamps for the current day
            long endOfDayInMillis = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            long startOfDayInMillis = calendar.getTimeInMillis();

            // Query call log data for the current day
            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    CallLog.Calls.DATE + " >= ? AND " + CallLog.Calls.DATE + " < ?",
                    new String[]{String.valueOf(startOfDayInMillis), String.valueOf(endOfDayInMillis)},
                    null
            );

            if (cursor != null) {
                int totalCalls = 0;

                while (cursor.moveToNext()) {
                    totalCalls++;
                }

                // Add total calls for the current day to the entries list
                entries.add(new Entry(day, totalCalls));

                // Store the total calls in the array for later use
                totalCallsArray[day] = totalCalls;

                // Close the cursor after processing
                cursor.close();
            }
        }

        // Create a LineDataSet from the entries
        LineDataSet dataSet = new LineDataSet(entries, "Total Calls");

        // Customize the LineDataSet (e.g., line color, circle color, etc.)
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);

        // Create a LineData object from the LineDataSet
        LineData lineData = new LineData(dataSet);

        // Set the LineData to the LineChart
        lineChart.setData(lineData);

        // Customize the LineChart x-axis labels
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates)); // Set date labels
        xAxis.setLabelRotationAngle(45f); // Rotate labels if needed
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Set label position

        // Customize the LineChart (e.g., labels, legend, animation, etc.)
        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1000); // Add animation
        lineChart.invalidate(); // Refresh the chart


        PieChart pieChart = findViewById(R.id.pieChart);

// Create an ArrayList to store call statistics for different types
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

// Query call log data for different call types
        int missedCalls = getMissedCallsForLast7Days();
        int incomingCalls = getIncomingCallsForLast7Days();
        int outgoingCalls = getOutgoingCallsForLast7Days();
        int rejectedCalls = getRejectedCallsForLast7Days();
        int blockedCalls = getBlockedCallsForLast7Days();

// Add data to the pieEntries ArrayList
        pieEntries.add(new PieEntry(missedCalls, "Missed"));
        pieEntries.add(new PieEntry(incomingCalls, "Incoming"));
        pieEntries.add(new PieEntry(outgoingCalls, "Outgoing"));
        pieEntries.add(new PieEntry(rejectedCalls, "Rejected"));
        pieEntries.add(new PieEntry(blockedCalls, "Blocked"));

// Create a PieDataSet from the pieEntries
        PieDataSet dataSet2 = new PieDataSet(pieEntries, "Call Types");
        dataSet2.setSliceSpace(3f);
        dataSet2.setSelectionShift(5f);

// Set colors for different call types
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.GRAY);
        dataSet2.setColors(colors);

// Create a PieData object from the PieDataSet
        PieData pieData = new PieData(dataSet2);

// Set the PieData to the PieChart
        pieChart.setData(pieData);

// Customize the PieChart (e.g., legend, hole radius, animation, etc.)
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.animateY(1000);
        pieChart.setUsePercentValues(true);

// Refresh the PieChart
        pieChart.invalidate();


        // Calculate total call duration for the last 7 days
        long totalCallDuration = 0;
        for (int day = 0; day < 7; day++) {
            // Calculate the start and end timestamps for the current day
            long endOfDayInMillis = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            long startOfDayInMillis = calendar.getTimeInMillis();

            // Query call log data for the current day
            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    new String[]{CallLog.Calls.DURATION},
                    CallLog.Calls.DATE + " >= ? AND " + CallLog.Calls.DATE + " < ?",
                    new String[]{String.valueOf(startOfDayInMillis), String.valueOf(endOfDayInMillis)},
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // Retrieve call duration and add it to the total
                    @SuppressLint("Range") long callDuration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
                    totalCallDuration += callDuration;
                }

                // Close the cursor after processing
                cursor.close();
            }
        }

        // Calculate total duration in hours and minutes
        long totalHours = totalCallDuration / 3600; // 3600 seconds in an hour
        long totalMinutes = (totalCallDuration % 3600) / 60; // 60 seconds in a minute

        // Update the TextViews with the calculated statistics for the current date
        TextView totalCallsTextView = findViewById(R.id.totalCallsTextView);
        totalCallsTextView.setText(String.valueOf(totalCallsArray[0]));

        TextView missedCallsAnswerTextView = findViewById(R.id.totalMissedCallsTextView);
        missedCallsAnswerTextView.setText(String.valueOf(getMissedCalls(totalCallsArray)));

        TextView incomingCallsAnswerTextView = findViewById(R.id.totalIncomingCallsTextView);
        incomingCallsAnswerTextView.setText(String.valueOf(getIncomingCalls(totalCallsArray)));

        TextView outgoingCallsAnswerTextView = findViewById(R.id.totalOutgoingCallsTextView);
        outgoingCallsAnswerTextView.setText(String.valueOf(getOutgoingCalls(totalCallsArray)));

        TextView rejectedCallsAnswerTextView = findViewById(R.id.totalRejectedCallsTextView);
        rejectedCallsAnswerTextView.setText(String.valueOf(getRejectedCalls(totalCallsArray)));

        TextView blockedCallsAnswerTextView = findViewById(R.id.totalBlockedCallsTextView);
        blockedCallsAnswerTextView.setText(String.valueOf(getBlockedCalls(totalCallsArray)));

        TextView totalDurationTextView = findViewById(R.id.totalDurationTextView);
        totalDurationTextView.setText(" " + totalHours + " hours " + totalMinutes + " minutes");
    }
    private int getMissedCallsForLast7Days() {
        int missedCalls = 0;
        Calendar calendar = Calendar.getInstance();
        for (int day = 0; day < 7; day++) {
            long endOfDayInMillis = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            long startOfDayInMillis = calendar.getTimeInMillis();

            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    CallLog.Calls.TYPE + " = ? AND " +
                            CallLog.Calls.DATE + " >= ? AND " +
                            CallLog.Calls.DATE + " < ?",
                    new String[]{
                            String.valueOf(CallLog.Calls.MISSED_TYPE),
                            String.valueOf(startOfDayInMillis),
                            String.valueOf(endOfDayInMillis)
                    },
                    null
            );

            if (cursor != null) {
                missedCalls += cursor.getCount();
                cursor.close();
            }
        }
        return missedCalls;
    }
    private int getIncomingCallsForLast7Days() {
        int incomingCalls = 0;
        Calendar calendar = Calendar.getInstance();
        for (int day = 0; day < 7; day++) {
            long endOfDayInMillis = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            long startOfDayInMillis = calendar.getTimeInMillis();

            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    CallLog.Calls.TYPE + " = ? AND " +
                            CallLog.Calls.DATE + " >= ? AND " +
                            CallLog.Calls.DATE + " < ?",
                    new String[]{
                            String.valueOf(CallLog.Calls.INCOMING_TYPE),
                            String.valueOf(startOfDayInMillis),
                            String.valueOf(endOfDayInMillis)
                    },
                    null
            );

            if (cursor != null) {
                incomingCalls += cursor.getCount();
                cursor.close();
            }
        }
        return incomingCalls;
    }
    // Method to get the count of outgoing calls for the last 7 days
    private int getOutgoingCallsForLast7Days() {
        int outgoingCalls = 0;
        Calendar calendar = Calendar.getInstance();
        for (int day = 0; day < 7; day++) {
            long endOfDayInMillis = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            long startOfDayInMillis = calendar.getTimeInMillis();

            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    CallLog.Calls.TYPE + " = ? AND " +
                            CallLog.Calls.DATE + " >= ? AND " +
                            CallLog.Calls.DATE + " < ?",
                    new String[]{
                            String.valueOf(CallLog.Calls.OUTGOING_TYPE),
                            String.valueOf(startOfDayInMillis),
                            String.valueOf(endOfDayInMillis)
                    },
                    null
            );

            if (cursor != null) {
                outgoingCalls += cursor.getCount();
                cursor.close();
            }
        }
        return outgoingCalls;
    }

    // Method to get the count of rejected calls for the last 7 days
    private int getRejectedCallsForLast7Days() {
        int rejectedCalls = 0;
        Calendar calendar = Calendar.getInstance();
        for (int day = 0; day < 7; day++) {
            long endOfDayInMillis = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            long startOfDayInMillis = calendar.getTimeInMillis();

            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    CallLog.Calls.TYPE + " = ? AND " +
                            CallLog.Calls.DATE + " >= ? AND " +
                            CallLog.Calls.DATE + " < ?",
                    new String[]{
                            String.valueOf(CallLog.Calls.REJECTED_TYPE),
                            String.valueOf(startOfDayInMillis),
                            String.valueOf(endOfDayInMillis)
                    },
                    null
            );

            if (cursor != null) {
                rejectedCalls += cursor.getCount();
                cursor.close();
            }
        }
        return rejectedCalls;
    }

    // Method to get the count of blocked calls for the last 7 days
    private int getBlockedCallsForLast7Days() {
        int blockedCalls = 0;
        Calendar calendar = Calendar.getInstance();
        for (int day = 0; day < 7; day++) {
            long endOfDayInMillis = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            long startOfDayInMillis = calendar.getTimeInMillis();

            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    CallLog.Calls.TYPE + " = ? AND " +
                            CallLog.Calls.DATE + " >= ? AND " +
                            CallLog.Calls.DATE + " < ?",
                    new String[]{
                            String.valueOf(CallLog.Calls.BLOCKED_TYPE),
                            String.valueOf(startOfDayInMillis),
                            String.valueOf(endOfDayInMillis)
                    },
                    null
            );

            if (cursor != null) {
                blockedCalls += cursor.getCount();
                cursor.close();
            }
        }
        return blockedCalls;
    }

    private int getMissedCalls(int[] totalCallsArray) {
        return totalCallsArray[1]; // Missed calls for the previous day
    }

    private int getIncomingCalls(int[] totalCallsArray) {
        return totalCallsArray[2]; // Incoming calls for the previous day
    }

    private int getOutgoingCalls(int[] totalCallsArray) {
        return totalCallsArray[3]; // Outgoing calls for the previous day
    }

    private int getRejectedCalls(int[] totalCallsArray) {
        return totalCallsArray[4]; // Rejected calls for the previous day
    }

    private int getBlockedCalls(int[] totalCallsArray) {
        return totalCallsArray[5]; // Blocked calls for the previous day
    }

}