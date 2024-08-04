package com.example.callhistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.callhistory.model.CallLogItem;
import com.example.callhistory.Adapter.CallLogAdapter;
import com.example.callhistory.Adapter.FilterAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
public class CallLogFragment extends Fragment implements FilterDialogFragment.FilterDialogListener {
    ProgressBar progressBar;

    private RecyclerView recyclerView;
    private FilterAdapter filterAdapter; // Use FilterAdapter to apply filters
    private List<CallLogItem> callLogItems = new ArrayList<>();
    private String selectedCallType = "All Calls";

    ImageView icon;
    Button incomingCallsButton ;
    private SwipeRefreshLayout swipeRefreshLayout;
   // private CallLogAdapter callLogAdapter;
    private SearchView searchView;
    private ProgressDialog progressDialog;
    private static final int CALL_PERMISSION_REQUEST_CODE = 123;
ImageView filterButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.call_log_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.search_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        filterButton = view.findViewById(R.id.filterIcon);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This code will be executed when the user swipes down to refresh

                // Fetch new data here
               // fetchData();
                loadCallLogData();
                // After fetching the new data, update your RecyclerView
                // For example, if you have an adapter, call notifyDataSetChanged()
                // adapter.notifyDataSetChanged();

                // Call setRefreshing(false) to indicate that the refresh is complete
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Set a listener for the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission if needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter your data based on the newText and update the RecyclerView
                filterAdapter.filter(newText);
                return true;
            }
        });
        // Initialize your RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Create an instance of FilterAdapter and pass the callLogItems
        filterAdapter = new FilterAdapter(requireContext());

        // Set the adapter for your RecyclerView
        recyclerView.setAdapter(filterAdapter);

        // Set the adapter for your RecyclerView
        recyclerView.setAdapter(filterAdapter); // Initially, display filtered data

        // Set an OnClickListener for the filter button to show the filter dialog
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });


        // Load and display the call log data
        loadCallLogData();
    }
    private void loadCallLogData() {
        // Query call log data
        Cursor cursor = requireContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int nameColumn = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int numberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateColumn = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationColumn = cursor.getColumnIndex(CallLog.Calls.DURATION);
            //int typeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE);

            do {
                String name = cursor.getString(nameColumn);
                String number = cursor.getString(numberColumn);
                long dateMillis = cursor.getLong(dateColumn);
                int duration = cursor.getInt(durationColumn);
               // int type = cursor.getInt(typeColumn);

                // Create a CallLogItem object and add it to the list
                CallLogItem callLogItem = new CallLogItem();
                callLogItem.setName(name);
                callLogItem.setNumber(number);

                // Convert the dateMillis to a formatted date string
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String date = dateFormat.format(new Date(dateMillis));
                callLogItem.setDate(date);

                // Convert duration and type to strings
                String durationStr = String.valueOf(duration);
              //  String typeStr = getCallType(type); // Set the correct call type here

                callLogItem.setTime(durationStr);
               // callLogItem.setCallType(typeStr); // Set the call type

                // Fetch call time and add it to the CallLogItem
                String callTime = getCallTimeFromMillis(dateMillis);
                callLogItem.setCallTime(callTime);

                callLogItems.add(callLogItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        // Notify the adapter that the dataset has changed
        filterAdapter.updateData(callLogItems);
    }


    private String getCallTimeFromMillis(long millis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return timeFormat.format(new Date(millis));
    }

    // Show the filter dialog
    private void showFilterDialog() {
        FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
        filterDialogFragment.setTargetFragment(this, 0);
        filterDialogFragment.show(getFragmentManager(), "filter_dialog");
    }

    @Override
    public void applyFilters(Bundle filterOptions) {
        // Apply filters using the filterAdapter
        filterAdapter.applyFilters(filterOptions);

        // Switch to displaying filtered data
        recyclerView.setAdapter(filterAdapter);
    }

    //@Override
    public void onFilterApply(Bundle filterOptions) {
        // Implement the logic to handle filter options when the user applies filters from the dialog.
        // This method is required to satisfy the FilterDialogListener interface.
    }
    @SuppressLint("Range")
    private void fetchCallLogs() {
        callLogItems.clear();


        Cursor cursor = requireContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            Map<String, List<CallLogItem>> groupedCallLogs = new HashMap<>();

            do {
                CallLogItem item = new CallLogItem();
                item.setName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
                item.setNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                long dateMillis = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                item.setDate(formatDate(dateMillis));
                item.setTime(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)));
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                item.setCallType(getCallType(type));

                String formattedDate = formatDateForGrouping(dateMillis);
                if (!groupedCallLogs.containsKey(formattedDate)) {
                    groupedCallLogs.put(formattedDate, new ArrayList<>());
                }
                groupedCallLogs.get(formattedDate).add(item);

            } while (cursor.moveToNext());

            cursor.close();

            //displayCallLog(groupedCallLogs);
        }
    }

    private String formatDateForGrouping(long dateMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date(dateMillis));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCallLogs();
        }
    }



    // Define the openFilterDialog method outside of applyFilters
    private void openFilterDialog() {
        FilterDialogFragment filterDialog = new FilterDialogFragment();
        filterDialog.setTargetFragment(CallLogFragment.this, 0);
        filterDialog.show(getFragmentManager(), "FilterDialogFragment");
    }



    private void buildCombinedList(Map<String, List<CallLogItem>> groupedCallLogs) {
        callLogItems.clear();

        // Get today's date in the required format
        String todayDate = formatDate(System.currentTimeMillis());

        // Check if today's date is present in the map
        List<CallLogItem> todayItems = groupedCallLogs.get(todayDate);
        if (todayItems!= null) {
            // Add today's date header and items
            CallLogItem headerItem = new CallLogItem();
            headerItem.setDate(todayDate);
            callLogItems.add(headerItem);
            callLogItems.addAll(todayItems);
            groupedCallLogs.remove(todayDate);
        }

        // Get the sorted list of dates
        List<String> sortedDates = new ArrayList<>(groupedCallLogs.keySet());
        Collections.sort(sortedDates);

        // Reverse the sorted list to achieve the desired order
        Collections.reverse(sortedDates);

        // Track the dates for which headers are already added
        Set<String> addedDateHeaders = new HashSet<>();

        // Iterate through the sorted and reversed dates
        for (String date : sortedDates) {
            List<CallLogItem> itemsForDate = groupedCallLogs.get(date);

            // Check if there are call log items for this date
            if (itemsForDate!= null &&!itemsForDate.isEmpty()) {
                // Add the date header if it's not already added
                if (!addedDateHeaders.contains(date)) {
                    CallLogItem headerItem = new CallLogItem();
                    headerItem.setDate(date);
                    callLogItems.add(headerItem);
                    addedDateHeaders.add(date);
                }

                // Add the associated call log items
                callLogItems.addAll(itemsForDate);
            }
        }
    }
    // Check if there are call log entries for the given date
    private boolean checkForCallLogEntries(String date) {
        for (CallLogItem item : callLogItems) {
            if (item.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    private String formatDate(long dateMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date(dateMillis));
    }


    private String getCallType(int callType) {
        switch (callType) {
            case CallLog.Calls.INCOMING_TYPE:
                return "📞"; // Incoming call symbol
            case CallLog.Calls.OUTGOING_TYPE:
                return "📱"; // Outgoing call symbol
            case CallLog.Calls.MISSED_TYPE:
                return "🔴"; // Missed call symbol
            case CallLog.Calls.BLOCKED_TYPE:
                return "🚫"; // Blocked call symbol
            case CallLog.Calls.REJECTED_TYPE:
                return "❌"; // Rejected call symbol
            default:
                return "❓"; // Unknown call type symbol
        }
    }

}
