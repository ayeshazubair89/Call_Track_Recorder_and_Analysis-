package com.example.callhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.callhistory.Adapter.CustomContactAdapter;
import com.example.callhistory.model.CustomContactItem;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.example.callhistory.model.CallLogStatics;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.Manifest;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_READ_CALL_LOG = 1;
    private static final String READ_CALL_LOG_PERMISSION = Manifest.permission.READ_CALL_LOG;
    private ProgressDialog progressDialog;
    private CardView cardView2,cardView,expandLayout2,ans_pie,expandLayout;
    private LinearLayout pie_qa;
    private static final int CATEGORY_ALL = 0;

    private static final int TIME_PERIOD_ALL = 0;
    private static final int TIME_PERIOD_TODAY = 1;
    private static final int TIME_PERIOD_YESTERDAY = 2;
    private static final int TIME_PERIOD_7_DAYS = 3;
    private static final int TIME_PERIOD_MONTH = 4;

    private int selectedTimePeriod = TIME_PERIOD_ALL; // Default to "All" initially

    private int currentCategory = CATEGORY_ALL;
    ProgressBar progressBar;
 /*   LineChart lineChart;*/
    RecyclerView recyclerView;
    Calendar calendar = Calendar.getInstance();
    long endTimeMillis = calendar.getTimeInMillis();
    ImageView icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  icon = findViewById(R.id.Icon2);

     /*   icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Yahan par icon par click hone par kya action karna chahte hain, wo code likhein
                // Example: Kuch toast message dikhana

                Intent intent = new Intent(MainActivity.this, Spalsh.class);
                startActivity(intent);
                finish();
            }
        });*/
        Button allButton = findViewById(R.id.all);
        Button todayButton = findViewById(R.id.today);
        Button yesterdayButton = findViewById(R.id.yesterday);
        Button weekButton = findViewById(R.id.week);
      //  Button monthButton = findViewById(R.id.month);

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();*/
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(MainActivity.this, TodayActivity.class);
                startActivity(intent);
                finish();*/
            }
        });

        yesterdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(MainActivity.this, Yesterday.class);
                startActivity(intent);
                finish();*/
            }
        });

        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent(MainActivity.this, Week.class);
                startActivity(intent);
                finish();*/
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



        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        PieChart pieChart = findViewById(R.id.pieChart);
       // readContactNamesAndCallCounts();

        cardView = findViewById(R.id.overall_cardview_dark);
        expandLayout2 = findViewById(R.id.hiddenLayout2);

        expandLayout = findViewById(R.id.hiddenLayout);

        // Animation 1: Right se Left
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(cardView, "translationX", 0f, -1000f);
        animation1.setDuration(2000); // Animation ka duration 2 seconds hai

        // Animation 2: Left se Right (Vapas Aana)
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(cardView, "translationX", 1000f, 0f);
        animation2.setDuration(2000); // Animation ka duration 2 seconds hai

        // Dono animations ko ek sath chalane ke liye AnimatorSet ka istemal karein
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animation1, animation2);

        // Animation shuru karein
        animatorSet.start();
        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);
        animationView.setAnimation(R.raw.analysis_anim); // Load the animation from the raw resource
        animationView.playAnimation();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list to hold your custom contact items
        // Create a list to hold your custom contact items
        List<CustomContactItem> customContactItems = new ArrayList<>();

// Replace the following block with your actual data retrieval logic

// Example: Retrieving data from the call log (replace this with your actual call log query)
        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            Map<String, Integer> contactCallCounts = new HashMap<>();

            while (cursor.moveToNext()) {
                // Retrieve caller name (if available) and phone number from the cursor
                @SuppressLint("Range") String callerName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

                // Determine the display name based on availability of callerName
                String displayName = (callerName != null && !callerName.isEmpty()) ? callerName : phoneNumber;

                // Check if the contact name already exists in the map
                if (contactCallCounts.containsKey(displayName)) {
                    // If it exists, increment the call count
                    int callCount = contactCallCounts.get(displayName);
                    contactCallCounts.put(displayName, callCount + 1);
                } else {
                    // If it doesn't exist, add a new entry to the map
                    contactCallCounts.put(displayName, 1);
                }
            }

            // Close the cursor after processing
            cursor.close();

            // Now, populate the customContactItems list using the map data
            for (Map.Entry<String, Integer> entry : contactCallCounts.entrySet()) {
                String contactName = entry.getKey();
                int callCount = entry.getValue();

                // Create a new CustomContactItem instance with the retrieved data
                CustomContactItem contactItem = new CustomContactItem(contactName, callCount);

                // Add the CustomContactItem to your list
                customContactItems.add(contactItem);
            }
        }

// Now, you have populated the customContactItems list with your call log data
// You can pass this list to your CustomContactAdapter for the RecyclerView
        CustomContactAdapter adapter = new CustomContactAdapter(customContactItems);
        recyclerView.setAdapter(adapter);


   /*     lineChart = findViewById(R.id.lineChart);*/
       progressDialog = new ProgressDialog(this);
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
        cardView2 = findViewById(R.id.overall_cardview2);
        // Initialize expandLayout2
        cardView2 = findViewById(R.id.overall_cardview2);
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


            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int menuItemId = item.getItemId();

                if (menuItemId == R.id.dashboard) {

                    progressDialog.setMessage("Loading Dialer..."); // Set your message
                    progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside
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
                }

                return false;
            }

        });

// Check for permission to read call log data
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_READ_CALL_LOG);
        } else {
            // Permission is granted, proceed to read call log data
            readCallLogData();
        }

            }
    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.filter_menu, popupMenu.getMenu());
        final int MENU_OPTION_1_ID = R.id.menu_option_1;
        final int MENU_OPTION_2_ID = R.id.menu_option_2;

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                long startTimeMillis;
                if (item.getItemId() == R.id.menu_option_1) {
                    // Create an Intent to open TodayActivity
                    startActivity(new Intent(MainActivity.this,TodayActivity.class));
                    finish();

                    return true;
                } else if (item.getItemId() == R.id.menu_option_2) {
                    startActivity(new Intent(MainActivity.this,Yesterday.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_3) {
                    startActivity(new Intent(MainActivity.this,Week.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_4) {
                    startActivity(new Intent(MainActivity.this,month.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_5) {
                    startActivity(new Intent(MainActivity.this,LocationActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_option_6) {
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                    finish();
                    return true;
                }else {

                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                    finish();
                    return false;
                }
            }
        });

        popupMenu.show();
    }
    private void readContactNamesAndCallCounts() {
        StringBuilder contactNames = new StringBuilder();
        // No need for a date filter, query all call log data
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

                if (callerName != null && !callerName.isEmpty()) {
                    // Call has a caller name
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



    private void startDialerActivity() {
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
        progressDialog.dismiss(); // Dismiss the ProgressDialog when starting DialerActivity
    }

    private void readCallLogData() {
        // Query call log data
        switch (selectedTimePeriod) {
            case TIME_PERIOD_TODAY:
                // Filter data for today
                Cursor cursor = queryCallLogForToday();
                break;
            case TIME_PERIOD_YESTERDAY:
                // Filter data for yesterday
                cursor = queryCallLogForYesterday();
                break;
            case TIME_PERIOD_7_DAYS:
                // Filter data for the last 7 days
                cursor = queryCallLogForLast7Days();
                break;
            case TIME_PERIOD_MONTH:
                // Filter data for the current month
                cursor = queryCallLogForCurrentMonth();
                break;
            case TIME_PERIOD_ALL:
            default:
                // Filter data for all calls
               /// cursor = queryAllCallLog();
                break;
        }


        Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            int totalCalls = 0;
            int missedCalls = 10;
            int incomingCalls = 30;
            int outgoingCalls = 20;
            int rejectedCalls = 5;
            int blockedCalls = 3;
            HashMap<String, Integer> callerFrequency = new HashMap<>();
            String longestCaller = "";
            long longestCallDuration = 0;
            long totalCallDuration = 0;
            // After setting up the Pie Chart...
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

           // PieData data = new PieData(dataSet);

            // Create a PieDataSet
            PieDataSet dataSet = new PieDataSet(pieEntries, "Call Types");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS); // You can customize colors here if needed

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
            PieData pieData = new PieData(dataSet);

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

            while (cursor.moveToNext()) {
                @SuppressLint("Range") String callType = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                @SuppressLint("Range") String callerNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
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

                // Update the frequency of the caller
                if (callerFrequency.containsKey(callerNumber)) {
                    callerFrequency.put(callerNumber, callerFrequency.get(callerNumber) + 1);
                } else {
                    callerFrequency.put(callerNumber, 1);
                }

                // Check for the longest call
                if (callDuration > longestCallDuration) {
                    longestCallDuration = callDuration;
                    longestCaller = callerNumber;
                }
                totalCallDuration += callDuration;

            }

            // Close the cursor after processing
            cursor.close();

            // Calculate total duration in hours and minutes
            long totalHours = totalCallDuration / 3600; // 3600 seconds in an hour
            long totalMinutes = (totalCallDuration % 3600) / 60; // 60 seconds in a minute


            // Update the TextViews with the calculated statistics
            TextView totalCallsTextView = findViewById(R.id.totalCallsTextView);
            totalCallsTextView.setText(String.valueOf(totalCalls));
            TextView missedCallsAnswerTextView2 = findViewById(R.id.totalMissedCallsTextViewPie);
            missedCallsAnswerTextView2.setText(String.valueOf(missedCalls));

            TextView missedCallsAnswerTextView = findViewById(R.id.totalMissedCallsTextView);
            missedCallsAnswerTextView.setText(String.valueOf(missedCalls));

            TextView incomingCallsAnswerTextView2 = findViewById(R.id.totalIncomingCallsTextViewPie);
            incomingCallsAnswerTextView2.setText(String.valueOf(incomingCalls));

            TextView incomingCallsAnswerTextView = findViewById(R.id.totalIncomingCallsTextView);
            incomingCallsAnswerTextView.setText(String.valueOf(incomingCalls));
            TextView outgoingCallsAnswerTextView2 = findViewById(R.id.totalOutgoingCallsTextViewPie);
            outgoingCallsAnswerTextView2.setText(String.valueOf(outgoingCalls));

            TextView rejectedCallsAnswerTextView2 = findViewById(R.id.totalRejectedCallsTextViewPie);
            rejectedCallsAnswerTextView2.setText(String.valueOf(rejectedCalls));
            TextView outgoingCallsAnswerTextView = findViewById(R.id.totalOutgoingCallsTextView);
            outgoingCallsAnswerTextView.setText(String.valueOf(outgoingCalls));

            TextView rejectedCallsAnswerTextView = findViewById(R.id.totalRejectedCallsTextView);
            rejectedCallsAnswerTextView.setText(String.valueOf(rejectedCalls));

            TextView blockedCallsAnswerTextView = findViewById(R.id.totalBlockedCallsTextView);
            blockedCallsAnswerTextView.setText(String.valueOf(blockedCalls));

            TextView frequentCallerAnswerTextView = findViewById(R.id.frequentCallerTextView);
            String frequentCallerInfo = getContactNameByPhoneNumber(getMostFrequentCaller(callerFrequency));
            frequentCallerAnswerTextView.setText(frequentCallerInfo);

            TextView longestCallerAnswerTextView = findViewById(R.id.longestCallerTextView);
            String longestCallerInfo = getContactNameByPhoneNumber(longestCaller);
            longestCallerAnswerTextView.setText(longestCallerInfo);

// Display the total duration
            TextView totalDurationTextView = findViewById(R.id.totalDurationTextView);
            totalDurationTextView.setText(" " + totalHours + " hours " + totalMinutes + " minutes");

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

// Replace these placeholders with actual call data for each day
            float callsOnDay0 = 10.0f; // Day 0 (today)
            float callsOnDay1 = 15.0f; // Day 1
            float callsOnDay2 = 20.0f; // Day 2
            float callsOnDay3 = 12.0f; // Day 3
            float callsOnDay4 = 18.0f; // Day 4
            float callsOnDay5 = 25.0f; // Day 5
            float callsOnDay6 = 8.0f;  // Day 6

// Populate the entries ArrayList with call data for each day
            entries.add(new Entry(0, callsOnDay0)); // Day 0 (today)
            entries.add(new Entry(1, callsOnDay1)); // Day 1
            entries.add(new Entry(2, callsOnDay2)); // Day 2
            entries.add(new Entry(3, callsOnDay3)); // Day 3
            entries.add(new Entry(4, callsOnDay4)); // Day 4
            entries.add(new Entry(5, callsOnDay5)); // Day 5
            entries.add(new Entry(6, callsOnDay6)); // Day 6

// Create a LineDataSet from the entries
            LineDataSet dataSet2 = new LineDataSet(entries, "Calls");

// Customize the LineDataSet (e.g., line color, circle color, etc.)
            dataSet2.setColor(Color.BLUE);
            dataSet2.setCircleColor(Color.BLUE);



        }
    }

    @SuppressLint("Range")
    private String getContactNameByPhoneNumber(String phoneNumber) {
        String contactName = phoneNumber; // Default to the phone number if contact name is not found

        // Query the contacts for the name associated with the phone number
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{phoneNumber},
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            cursor.close();
        }

        return contactName;
    }
    private String getMostFrequentCaller(HashMap<String, Integer> callerFrequency) {
        String frequentCaller = "";
        int maxFrequency = 0;

        for (Map.Entry<String, Integer> entry : callerFrequency.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                frequentCaller = entry.getKey();
            }
        }

        return frequentCaller;
    }
    private Cursor queryCallLogForToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0); // Set the time to midnight
        long startOfDayMillis = calendar.getTimeInMillis(); // Start of today

        calendar.set(Calendar.HOUR_OF_DAY, 23); // Set the time to 11:59:59 PM
        long endOfDayMillis = calendar.getTimeInMillis(); // End of today

        // Define the projection to retrieve the required call log fields
        String[] projection = {
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        // Define the selection criteria (filter by date range)
        String selection = CallLog.Calls.DATE + " >= ? AND " +
                CallLog.Calls.DATE + " <= ?";

        // Define the selection arguments (start and end timestamps)
        String[] selectionArgs = {
                String.valueOf(startOfDayMillis),
                String.valueOf(endOfDayMillis)
        };

        // Sort the results by call date (ascending order)
        String sortOrder = CallLog.Calls.DATE + " ASC";

        // Perform the query and return the cursor
        return getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

// Implement similar methods for other time periods (e.g., queryCallLogForYesterday, queryCallLogForLast7Days, queryCallLogForCurrentMonth, etc.)
private Cursor queryCallLogForYesterday() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1); // Subtract one day to get yesterday

    long startOfDayMillis = getStartOfDayMillis(calendar);
    long endOfDayMillis = getEndOfDayMillis(calendar);

    return queryCallLogBetweenDates(startOfDayMillis, endOfDayMillis);
}

    private Cursor queryCallLogForLast7Days() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6); // Subtract six days to get the last 7 days

        long startOfDayMillis = getStartOfDayMillis(calendar);
        long endOfDayMillis = getEndOfDayMillis(Calendar.getInstance()); // End date is today

        return queryCallLogBetweenDates(startOfDayMillis, endOfDayMillis);
    }

    private Cursor queryCallLogForCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the current month

        long startOfDayMillis = getStartOfDayMillis(calendar);
        long endOfDayMillis = getEndOfDayMillis(Calendar.getInstance()); // End date is today

        return queryCallLogBetweenDates(startOfDayMillis, endOfDayMillis);
    }

    // Helper method to query call log data between two dates
    private Cursor queryCallLogBetweenDates(long startDateMillis, long endDateMillis) {
        // Define the projection to retrieve the required call log fields
        String[] projection = {
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        // Define the selection criteria (filter by date range)
        String selection = CallLog.Calls.DATE + " >= ? AND " +
                CallLog.Calls.DATE + " <= ?";

        // Define the selection arguments (start and end timestamps)
        String[] selectionArgs = {
                String.valueOf(startDateMillis),
                String.valueOf(endDateMillis)
        };

        // Sort the results by call date (ascending order)
        String sortOrder = CallLog.Calls.DATE + " ASC";

        // Perform the query and return the cursor
        return getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    // Helper method to get the start of the day timestamp (midnight)
    private long getStartOfDayMillis(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0); // Set the time to midnight
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    // Helper method to get the end of the day timestamp (11:59:59 PM)
    private long getEndOfDayMillis(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23); // Set the time to 11:59:59 PM
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, read call log data
                readCallLogData();
                readContactNamesAndCallCounts();
            } else {
                // Permission denied, handle it as needed (e.g., show a message)
            }
        }
    }




}
