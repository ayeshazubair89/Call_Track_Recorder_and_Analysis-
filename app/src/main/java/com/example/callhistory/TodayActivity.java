package com.example.callhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TodayActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private CardView cardView2,cardView,expandLayout2,ans_pie;
    private LinearLayout expandLayout,pie_qa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        Button allButton = findViewById(R.id.all);
        Button todayButton = findViewById(R.id.today);
        Button yesterdayButton = findViewById(R.id.yesterday);
        Button weekButton = findViewById(R.id.week);
        //  Button monthButton = findViewById(R.id.month);

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayActivity.this, TodayActivity.class);
                startActivity(intent);
                finish();
            }
        });

        yesterdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayActivity.this, Yesterday.class);
                startActivity(intent);
                finish();
            }
        });

        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayActivity.this, Week.class);
                startActivity(intent);
                finish();
            }
        });

       /* monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTimePeriod = TIME_PERIOD_MONTH;
                // Call a method to update your data based on the selected time period (e.g., readCallLogData())
                readCallLogData();
            }
        });*/

        pie_qa = findViewById(R.id.pie_qa);
        ans_pie = findViewById(R.id.ans_pie);
        pie_qa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the current visibility of the LinearLayout
                if (ans_pie.getVisibility() == View.GONE) {
                    // If it's gone, make it visible
                    ans_pie.setVisibility(View.VISIBLE);
                } else {
                    // If it's visible, make it gone
                    ans_pie.setVisibility(View.GONE);
                }
            }
        });

        cardView = findViewById(R.id.overall_cardview);
        expandLayout = findViewById(R.id.hiddenLayout);
        cardView2 = findViewById(R.id.overall_cardview2);
        expandLayout2 = findViewById(R.id.hiddenLayout2);
        readCallLogDataForCurrentDate();
        readContactNamesAndCallCounts();
        long startOfDayInMillis = getStartOfDayInMillis(0); // Today
        long startOfDayInMillisYesterday = getStartOfDayInMillis(1); // Yesterday
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int menuItemId = item.getItemId();

                if (menuItemId == R.id.dashboard) {
                  /*  Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.show();
                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
                            overridePendingTransition(0, 0);
                           // startDialerActivity();
                        }
                    }, 1000); // Delay for 2 seconds (you can adjust this as needed)
*/
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
    private long getStartOfDayInMillis(int dayOffset) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -dayOffset); // Apply the day offset
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    private void readCallLogDataForCurrentDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Query call log data for the current date
        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            int totalCalls = 0;
            int missedCalls = 0;
            int incomingCalls = 0;
            int outgoingCalls = 0;
            int rejectedCalls = 0;
            int blockedCalls = 0;
            long totalCallDuration = 0;

            while (cursor.moveToNext()) {
                // Retrieve call date
                @SuppressLint("Range") long callDateInMillis = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                String callDate = dateFormat.format(new Date(callDateInMillis));

                if (callDate.equals(currentDate)) {
                    // Call belongs to the current date, update statistics
                    @SuppressLint("Range") String callType = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                    @SuppressLint("Range") long callDuration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));

                    totalCalls++;

                    switch (Integer.parseInt(callType)) {
                        case CallLog.Calls.INCOMING_TYPE:
                            incomingCalls++;
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            outgoingCalls++;
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            missedCalls++;
                            break;
                        case CallLog.Calls.REJECTED_TYPE:
                            rejectedCalls++;
                            break;
                        case CallLog.Calls.BLOCKED_TYPE:
                            blockedCalls++;
                            break;
                    }

                    totalCallDuration += callDuration;
                }
            }

            // Close the cursor after processing
            cursor.close();

            LineChart lineChart = findViewById(R.id.lineChart);

            // Customize the appearance of the chart
            lineChart.setDrawGridBackground(false);
            lineChart.getDescription().setEnabled(false);

            // Customize the X-axis
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f); // Display one label per data point

            // Customize the Y-axis
            YAxis leftAxis = lineChart.getAxisLeft();
            YAxis rightAxis = lineChart.getAxisRight();
            leftAxis.setAxisMinimum(0f); // Minimum value for the Y-axis
            rightAxis.setAxisMinimum(0f);

            // Prepare your data for the chart (replace this with your data)
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, 5f)); // Replace 5f with your data
            entries.add(new Entry(1, 10f)); // Replace 10f with your data
            // Add more data points as needed

            LineDataSet dataSet = new LineDataSet(entries, "Line Chart Data");

            // Customize the appearance of the data set
            dataSet.setColor(Color.BLUE);
            dataSet.setLineWidth(2f);
            dataSet.setValueTextSize(12f);
            dataSet.setDrawCircles(true);
            dataSet.setDrawValues(true);

            // Create a LineData object from the data set
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);
            LineData lineData = new LineData(dataSets);

            // Set the data to the chart
            lineChart.setData(lineData);

            // Refresh the chart
            lineChart.invalidate();


            PieChart pieChart = findViewById(R.id.pieChart);
            // Declare pieEntries as a List<PieEntry>
            List<PieEntry> pieEntries = new ArrayList<>();
            ArrayList<String> pieLabels = new ArrayList<>();
            pieLabels.add("Missed");
            pieLabels.add("Incoming");
            pieLabels.add("Outgoing");
            pieLabels.add("Rejected");
            pieLabels.add("Blocked");
            // Add the call statistics to the PieEntries without labels
            pieEntries.add(new PieEntry(missedCalls));
            pieEntries.add(new PieEntry(incomingCalls));
            pieEntries.add(new PieEntry(outgoingCalls));
            pieEntries.add(new PieEntry(rejectedCalls));
            pieEntries.add(new PieEntry(blockedCalls));

          /*// Add the call statistics to the PieEntries
            pieEntries.add(new PieEntry(missedCalls,"Missed"));
            pieEntries.add(new PieEntry(incomingCalls, "Incoming"));
            pieEntries.add(new PieEntry(outgoingCalls, "Outgoing"));
            pieEntries.add(new PieEntry(rejectedCalls, "Rejected"));
            pieEntries.add(new PieEntry(blockedCalls, "Blocked"));*/
            // PieData data = new PieData(dataSet);

            // Create a PieDataSet
            PieDataSet dataSet2 = new PieDataSet(pieEntries, "Call Types");
            dataSet2.setSliceSpace(3f);
            dataSet2.setSelectionShift(5f);
            dataSet2.setColors(ColorTemplate.JOYFUL_COLORS); // You can customize colors here if needed

            int redColor = ContextCompat.getColor(this, R.color.red);
            int GreyColor = ContextCompat.getColor(this, R.color.gray);
            int YellowColor = ContextCompat.getColor(this, R.color.yellow);

            int blueColor = ContextCompat.getColor(this, R.color.blue);
            int greenColor = ContextCompat.getColor(this, R.color.green);
// Customize the PieDataSet
       /*     dataSet.setColors(new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.GRAY});
            dataSet.setValueFormatter(new PercentFormatter(pieChart));
            dataSet.setColors(redColor, greenColor, blueColor,YellowColor,GreyColor);
            dataSet.setValueTextSize(12f);*/
            Legend legend = pieChart.getLegend();
            legend.setWordWrapEnabled(true); // Enable word wrap for legend
            legend.setTextSize(12f); // Set text size for legend
            legend.setFormSize(12f); // Set form (shape) size for legend
            legend.setForm(Legend.LegendForm.CIRCLE); // Set form to circles (you can change this to your preferred shape)
            legend.setXEntrySpace(10f); // Set horizontal space between legend entries
            legend.setYEntrySpace(5f); // Set vertical space between legend entries
            legend.setOrientation(Legend.LegendOrientation.VERTICAL); // Set legend orientation
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Set legend vertical alignment
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Set legend horizontal alignment


// Create a PieData object
            PieData pieData = new PieData(dataSet2);

// Set the PieData to the chart
            pieChart.setData(pieData);

// Additional chart customization can be done here
            pieChart.getDescription().setEnabled(false);
            pieChart.setHoleRadius(30f);
            pieChart.setTransparentCircleRadius(40f);
            pieChart.animateY(1000);
            pieChart.setUsePercentValues(true);

// Refresh the chart
            pieChart.invalidate();
            // Calculate total duration in hours and minutes
            long totalHours = totalCallDuration / 3600; // 3600 seconds in an hour
            long totalMinutes = (totalCallDuration % 3600) / 60; // 60 seconds in a minute

            // Update the TextViews with the calculated statistics for the current date
            TextView totalCallsTextView = findViewById(R.id.totalCallsTextView);
            totalCallsTextView.setText(String.valueOf(totalCalls));

            TextView missedCallsAnswerTextView = findViewById(R.id.totalMissedCallsTextView);
            missedCallsAnswerTextView.setText(String.valueOf(missedCalls));

            TextView incomingCallsAnswerTextView = findViewById(R.id.totalIncomingCallsTextView);
            incomingCallsAnswerTextView.setText(String.valueOf(incomingCalls));

            TextView outgoingCallsAnswerTextView = findViewById(R.id.totalOutgoingCallsTextView);
            outgoingCallsAnswerTextView.setText(String.valueOf(outgoingCalls));

            TextView rejectedCallsAnswerTextView = findViewById(R.id.totalRejectedCallsTextView);
            rejectedCallsAnswerTextView.setText(String.valueOf(rejectedCalls));

            TextView blockedCallsAnswerTextView = findViewById(R.id.totalBlockedCallsTextView);
            blockedCallsAnswerTextView.setText(String.valueOf(blockedCalls));

            TextView totalDurationTextView = findViewById(R.id.totalDurationTextView);
            totalDurationTextView.setText(" " + totalHours + " hours " + totalMinutes + " minutes");
        }
    }
    private void readContactNamesAndCallCounts() {
        StringBuilder contactNames = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Query call log data for the current date
        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            HashMap<String, Integer> contactCallCounts = new HashMap<>();

            while (cursor.moveToNext()) {
                @SuppressLint("Range") String callerName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                @SuppressLint("Range") String callDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))));

                if (callDate.equals(currentDate) && callerName != null && !callerName.isEmpty()) {
                    // Call belongs to the current date and has a caller name
                    int callCount = contactCallCounts.getOrDefault(callerName, 0);
                    contactCallCounts.put(callerName, callCount + 1);
                }
            }

            // Close the cursor after processing
            cursor.close();

            // Build the text to display in the TextView
            for (Map.Entry<String, Integer> entry : contactCallCounts.entrySet()) {
                String callerName = entry.getKey();
                int callCount = entry.getValue();
                contactNames.append(callerName).append(" - ").append(callCount).append(" calls\n");
            }

            // Update the TextView with the contact names and call counts
            TextView contactNamesTextView = findViewById(R.id.contactNamesTextView);
            contactNamesTextView.setText(contactNames.toString());
        }
    }

/*    public void showPopupMenu(View view) {
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
                    startActivity(new Intent(TodayActivity.this,TodayActivity.class));
                    finish();

                    return true;
                } else if (item.getItemId() == R.id.menu_option_2) {
                    startActivity(new Intent(TodayActivity.this,Yesterday.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_3) {
                    startActivity(new Intent(TodayActivity.this,Week.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_4) {
                    startActivity(new Intent(TodayActivity.this,month.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_5) {
                    startActivity(new Intent(TodayActivity.this,year.class));
                    finish();
                    return true;
                } else {


                    return false;
                }
            }
        });

        popupMenu.show();
    }*/
}
