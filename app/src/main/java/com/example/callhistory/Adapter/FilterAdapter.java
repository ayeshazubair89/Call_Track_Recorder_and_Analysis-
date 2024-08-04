package com.example.callhistory.Adapter; // Update the package name accordingly


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callhistory.FilterDialogFragment;
import com.example.callhistory.HistoryOnClick;
import com.example.callhistory.R;
import com.example.callhistory.model.CallHistoryItem;
import com.example.callhistory.model.CallLogItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.Toast;

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FilterDialogFragment.FilterDialogListener {
    private List<CallLogItem> originalCallLogItems;
 // Initialize this list with call log data
    private List<CallLogItem> filteredCallLogItems;
    private String lastDate = "";
    private static final int CALL_PERMISSION_REQUEST_CODE = 1; // or any other integer value

    private Context context;




    private Set<String> uniqueDateHeaders = new HashSet<>(); // Declare uniqueDateHeaders here


    private String selectedSortOption = "Sort by Date (Newest First)"; // Default sorting option
    private String selectedCallType /*= "All Calls"*/; // Default value

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    // Define a mapping of call type names to image resource IDs
    private Map<String, Integer> callTypeToImageMap = new HashMap<>();
    public FilterAdapter(Context context) {
        this.context = context;
        // Initialize the call type to image mapping

        // Initialize originalCallLogItems with call log data during adapter construction
        this.originalCallLogItems = getCallLogData();
        this.filteredCallLogItems = new ArrayList<>(originalCallLogItems); // Initialize filteredCallLogItems with all data
        // Sort the originalCallLogItems list in ascending order by date
        Collections.sort(originalCallLogItems, new Comparator<CallLogItem>() {
            @Override
            public int compare(CallLogItem item1, CallLogItem item2) {
                return item1.getDate().compareTo(item2.getDate());
            }
        });
        // Group call logs by date
        groupCallLogsByDate();
    }


    @Override
    public int getItemViewType(int position) {
        return filteredCallLogItems.get(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            // Create a header view
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_header_item, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            // Create an item view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_item, parent, false);
            return new ViewHolder(itemView);
        }
    }

   @Override
   public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       CallLogItem item = filteredCallLogItems.get(position);

       if (holder instanceof HeaderViewHolder) {
           HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

           // Display the date header only if it's different from the previous item
           if (position == 0 || !formatDateHeader(item.getDate()).equals(formatDateHeader(filteredCallLogItems.get(position - 1).getDate()))) {
               headerViewHolder.dateTextView.setVisibility(View.VISIBLE);
               headerViewHolder.dateTextView.setText(formatDateHeader(item.getDate()));
           } else {
               headerViewHolder.dateTextView.setVisibility(View.GONE);
           }
       } else if (holder instanceof ViewHolder) {
           ViewHolder viewHolder = (ViewHolder) holder;

           // Set item details in the ViewHolder
           viewHolder.nameTextView.setText(item.getName());
           viewHolder.numberTextView.setText(item.getNumber());
           if (item.getTime() != null) {
               viewHolder.timeTextView.setText(item.getTime()); // Set the call time here
           } else {
               viewHolder.timeTextView.setText(""); // Set some default text or handle the null case
           }

           if (item.getDuration() != null) {
               viewHolder.TimeClockTextView.setText(item.getDuration()); // Set the call duration here
           } else {
               viewHolder.TimeClockTextView.setText(""); // Set some default text or handle the null case
           }
           // Set the call duration here
           viewHolder.typeTextView.setText(item.getCallType());

           viewHolder.arrowImageView.setImageResource(item.getArrowColorResId());

           // Set text color based on call type
           int colorResId = R.color.default_call_type_color;
           switch (item.getCallType()) {
               case "missed calls":
                   colorResId = R.color.missed_call_color;
                   break;
               case "incoming calls":
                   colorResId = R.color.incoming_call_color;
                   break;
               case "outgoing calls":
                   colorResId = R.color.outgoing_call_color;
                   break;
           }
           int color = ContextCompat.getColor(context, colorResId);
           viewHolder.typeTextView.setTextColor(color);
           viewHolder.bind(item);
       }

   }


    private String formatDateHeader(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the time zone to UTC

        try {
            Date date = dateFormat.parse(dateString);
            Calendar now = Calendar.getInstance();
            Calendar itemDate = Calendar.getInstance();
            itemDate.setTime(date);

            if (DateUtils.isToday(date.getTime())) {
                return "Today";
            } else if (DateUtils.isToday(date.getTime() + DateUtils.DAY_IN_MILLIS)) {
                return "Yesterday";
            } else {
                SimpleDateFormat headerDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return headerDateFormat.format(date);
            }
        } catch (ParseException e) {
            // Handle date parsing errors here
            e.printStackTrace();
            return dateString; // Return the original string if parsing fails
        }
    }

    private String formatTime(String seconds) {
        int totalSeconds = Integer.parseInt(seconds);
        int minutes = totalSeconds / 60;
        int remainingSeconds = totalSeconds % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds);
    }



    @Override
    public int getItemCount() {
        return filteredCallLogItems.size();
    }

    public void onFilterApply(Bundle filterOptions) {
        selectedSortOption = filterOptions.getString("sortOption");
        applyFilters(filterOptions);
    }


    @Override
    public void applyFilters(Bundle filterOptions) {
        if (filterOptions == null) {
            // Handle the case where the filterOptions is null.
            Log.d("FilterDebug", "Filter options are null");
            return;
        }

        try {
            // Extract filter options from the Bundle
            String selectedSortOption = filterOptions.getString("sortOption");
            String startDate = filterOptions.getString("startDate");
            String endDate = filterOptions.getString("endDate");
            String minDuration = filterOptions.getString("minDuration");
            String maxDuration = filterOptions.getString("maxDuration");
            String startTime = filterOptions.getString("startTime");
            String endTime = filterOptions.getString("endTime");

            boolean isIncomingSelected = filterOptions.getBoolean("incomingSelected");
            boolean isOutgoingSelected = filterOptions.getBoolean("outgoingSelected");
            boolean isMissedSelected = filterOptions.getBoolean("missedSelected");

            Log.d("FilterDebug", "Incoming Selected: " + isIncomingSelected);
            Log.d("FilterDebug", "Outgoing Selected: " + isOutgoingSelected);
            Log.d("FilterDebug", "Missed Selected: " + isMissedSelected);

            String defaultStartDate = "2022-11-01"; // Replace with your default start date

            // Check if start date is null or empty, and assign default if needed
            if (startDate == null || startDate.isEmpty()) {
                startDate = defaultStartDate;
            }

            // Check if end date is null or empty, and assign default if needed
            if (endDate == null || endDate.isEmpty()) {
                // Get the current date and format it as "YYYY-MM-DD"
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the time zone to UTC
                Date currentDate = new Date();
                endDate = dateFormat.format(currentDate);
            }

            // Create separate lists for each filter stage
            List<CallLogItem> dateFiltered = new ArrayList<>();
            List<CallLogItem> timeFiltered = new ArrayList<>();
            List<CallLogItem> durationFiltered = new ArrayList<>();

            // Assuming you have a method to retrieve call log data
            List<CallLogItem> callLogData = getCallLogData(); // You need to implement this

            // Define date and time formats with proper time zone
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the time zone to UTC
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

            // Parse start and end dates
            Date filterStartDate = dateFormat.parse(startDate);
            Date filterEndDate = dateFormat.parse(endDate);

            // Parse start and end times (Added time parsing)
            Date filterStartTime = timeFormat.parse(startTime);
            Date filterEndTime = timeFormat.parse(endTime);

            // Parse duration limits
            int minDurationInt = parseDurationToInt(minDuration);
            int maxDurationInt = parseDurationToInt(maxDuration);

            // Apply Date Filter to the Call Log Data
            for (CallLogItem item : callLogData) {
                // Check if no specific call type is selected, or if a specific call type is selected, check if it matches
                if ((!isIncomingSelected && !isOutgoingSelected && !isMissedSelected) ||
                        (isIncomingSelected && item.getCallType().equals("1")) ||
                        (isOutgoingSelected && item.getCallType().equals("2")) ||
                        (isMissedSelected && item.getCallType().equals("3"))) {
                    Date callDate = dateFormat.parse(item.getDate());
                    if (callDate != null) {
                        // Compare the dates using getTime() to get a long value
                        long callDateTime = callDate.getTime();
                        long filterStartDateTime = filterStartDate.getTime();
                        long filterEndDateTime = filterEndDate.getTime();
                        if (callDateTime >= filterStartDateTime && callDateTime <= filterEndDateTime) {
                            dateFiltered.add(item);
                        }
                    }
                }
            }

            // Apply Time Filter to the Date Filtered List
            for (CallLogItem item : dateFiltered) {
                Date callTime = timeFormat.parse(item.getTime());
                if (callTime != null && callTime.after(filterStartTime) && callTime.before(filterEndTime)) {
                    timeFiltered.add(item);
                }
            }

            // Apply Duration Filter to the Time Filtered List
            for (CallLogItem item : timeFiltered) {
                int callDurationSeconds = parseDurationToInt(item.getDuration());
                if (callDurationSeconds >= minDurationInt && callDurationSeconds <= maxDurationInt) {
                    durationFiltered.add(item);
                }
            }

            // Combine the filtered results based on the selected sort option
            List<CallLogItem> finalFilteredList = new ArrayList<>();

            if (selectedSortOption.equals("Sort by Date (Oldest First)")) {
                finalFilteredList.addAll(durationFiltered); // Add durationFiltered
            } else if (selectedSortOption.equals("Sort by Date (Newest First)")) {
                finalFilteredList.addAll(durationFiltered); // Add durationFiltered
                Collections.reverse(finalFilteredList); // Reverse the list for newest first
            }

            // Debugging log: Print the number of items in the final filtered list
            Log.d("FilterDebug", "Number of items in the final filtered list: " + finalFilteredList.size());

            // Update the dataset with the sorted and filtered list
            filteredCallLogItems.clear();
            filteredCallLogItems.addAll(finalFilteredList);
            notifyDataSetChanged();

        } catch (Exception e) {
            // Handle any exceptions that may occur during extraction
            Log.e("FilterDebug", "Error applying filters: " + e.getMessage());
        }
    }


    // Helper method to parse duration in "HH:mm:ss" format to seconds
    private int parseDurationToSeconds(String durationStr) {
        try {
            String[] parts = durationStr.split(":");
            if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);
                return hours * 3600 + minutes * 60 + seconds;
            }
        } catch (NumberFormatException e) {
            // Handle parsing errors here
            Log.e("FilterDebug", "Error parsing duration to seconds: " + e.getMessage());
        }
        return -1; // Return an invalid value if parsing fails
    }

    // Helper method to parse duration in "HH:mm:ss" format to integer value (e.g., seconds)
    private int parseDurationToInt(String durationStr) {
        try {
            String[] parts = durationStr.split(":");
            if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = Integer.parseInt(parts[2]);
                return hours * 3600 + minutes * 60 + seconds;
            }
        } catch (NumberFormatException e) {
            // Handle parsing errors here
            Log.e("FilterDebug", "Error parsing duration to int: " + e.getMessage());
        }
        return 0;
    }

        public void updateData(List<CallLogItem> data) {
        originalCallLogItems.clear();
        originalCallLogItems.addAll(data);
        // Regroup call logs by date
        groupCallLogsByDate();
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }


    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    private void groupCallLogsByDate() {
        // Create a new list for filtered items
        List<CallLogItem> filteredList = new ArrayList<>();

        // Initialize a variable to keep track of the last date
        String lastDate = null;

        for (CallLogItem item : filteredCallLogItems) {
            if (!item.isHeader()) {
                String date = item.getDate();
                if (!date.equals(lastDate)) {
                    // Add a date header item only if it's a unique date
                    CallLogItem dateHeader = new CallLogItem(date, true);
                    filteredList.add(dateHeader);

                    // Update the lastDate to the current date
                    lastDate = date;
                }
            }
            // Add the item (either regular call log item or date header) to the filtered list
            filteredList.add(item);
        }

        // Update filteredCallLogItems with the filtered list
        filteredCallLogItems.clear();
        filteredCallLogItems.addAll(filteredList);
    }

    public class CallLogConstants {
        public static final int CALL_TYPE_INCOMING = 1;
        public static final int CALL_TYPE_OUTGOING = 2;
        public static final int CALL_TYPE_MISSED = 3;
        // Add more call types if needed
    }

    private List<CallLogItem> getCallLogData() {
        List<CallLogItem> callLogItems = new ArrayList<>();

        // Query call log data
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int nameColumn = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int numberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int dateColumn = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationColumn = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int typeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE);

            do {
                String name = cursor.getString(nameColumn);
                String number = cursor.getString(numberColumn);
                long dateMillis = cursor.getLong(dateColumn);
                int duration = cursor.getInt(durationColumn);
                int type = cursor.getInt(typeColumn);

                // Convert the dateMillis to a formatted date string
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String date = dateFormat.format(new Date(dateMillis));

                // Create a CallLogItem object and add it to the list
                CallLogItem callLogItem = new CallLogItem();
                callLogItem.setName(name);
                callLogItem.setNumber(number);
                callLogItem.setDate(date);
                callLogItem.setTime(formatTime(dateMillis));
                callLogItem.setDuration(formatDuration(duration));
                callLogItem.setCallType(String.valueOf(type));
                callLogItem.setArrowColorResId(getArrowColorResId(String.valueOf(type)));

                callLogItems.add(callLogItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return callLogItems;
    }

    public int getArrowColorResId(String callType) {
        int colorResId;

        if ("1".equalsIgnoreCase(callType)) {
            colorResId = R.drawable.baseline_call_received;
        } else if ("2".equalsIgnoreCase(callType)) {
            colorResId = R.drawable.outgoing;
        } else if ("3".equalsIgnoreCase(callType)) {
            colorResId = R.drawable.baseline_call_missed;
        } else if ("4".equalsIgnoreCase(callType)) {
            colorResId = R.drawable.baseline_call_missed_outgoing;
        } else if ("rejected calls".equalsIgnoreCase(callType)) {
            colorResId = R.drawable.baseline_block;
        } else {
            colorResId = R.drawable.baseline_block; // Default arrow color resource ID for unknown call types
        }

        return colorResId;
    }
    public void filter(String searchText) {
        filteredCallLogItems.clear();
        if (TextUtils.isEmpty(searchText)) {
            filteredCallLogItems.addAll(originalCallLogItems);
        } else {
            searchText = searchText.toLowerCase(Locale.getDefault());
            for (CallLogItem item : originalCallLogItems) {
                String name = item.getName();
                String number = item.getNumber();
                if (name != null && number != null &&
                        (name.toLowerCase(Locale.getDefault()).contains(searchText) ||
                                number.toLowerCase(Locale.getDefault()).contains(searchText))) {
                    filteredCallLogItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }


    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds);
    }

    private String formatTime(long dateMillis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return timeFormat.format(new Date(dateMillis));
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView numberTextView;
        TextView timeTextView;
        TextView TimeClockTextView;
        TextView typeTextView;
        ImageView contactInitialImageView;
        RecyclerView recyclerView;
        ImageView arrowImageView;
        private View dialogView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            numberTextView = itemView.findViewById(R.id.numberTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            TimeClockTextView = itemView.findViewById(R.id.TimeClockTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            contactInitialImageView = itemView.findViewById(R.id.contactInitialImageView);
            arrowImageView= itemView.findViewById(R.id.arrowImageView);

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           if (originalCallLogItems != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                                CallLogItem callLogItem = originalCallLogItems.get(getAdapterPosition());
                               showContactOptionsDialog(callLogItem);

                            }

                        }
                    });



                }
            });*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CallLogItem callLogItem = originalCallLogItems.get(position);
                        String phoneNumber = callLogItem.getNumber();
                        // Assuming you have a valid implementation of getCallHistory method
                        // Call the method to fetch call history for the clicked phone number
                        fetchAndShowCallHistory(phoneNumber);
                    }
                }
            });

        }
        private void fetchAndShowCallHistory(String phoneNumber) {
            // Assuming you have a valid implementation of fetching call history for the given phone number
            List<CallHistoryItem> callHistory = fetchCallHistory(phoneNumber);

            // Construct the message to display in the dialog
            StringBuilder messageBuilder = new StringBuilder();
            for (CallHistoryItem historyItem : callHistory) {
                messageBuilder.append("Type: ").append(historyItem.getCallType()).append("\n")
                        .append("Time: ").append(historyItem.getFormattedTime()).append("\n")
                        .append("Duration: ").append(historyItem.getFormattedDuration()).append("\n\n");
            }

            // Show the dialog with the call history
            showDialogWithTitleAndMessage("Call History for " + phoneNumber, messageBuilder.toString());
        }


        private void showDialogWithTitleAndMessage(String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }


        private List<CallHistoryItem> fetchCallHistory(String phoneNumber) {
            // Here you should implement your logic to fetch call history from your data source
            // This is a dummy implementation for demonstration purposes
            List<CallHistoryItem> callHistory = new ArrayList<>();
            callHistory.add(new CallHistoryItem("2024-02-25", "5 mins", "Incoming"));
            callHistory.add(new CallHistoryItem("2024-02-24", "3 mins", "Outgoing"));
            // Add more call history items as needed
            return callHistory;
        }


        public void bind(CallLogItem originalCallLogItem) {
               // Set the contact initial as an image
            if (originalCallLogItem.getName() != null && !originalCallLogItem.getName().isEmpty()) {
                String initial = originalCallLogItem.getName().substring(0, 1).toUpperCase();
                Drawable textDrawable = createTextDrawable(initial);
                contactInitialImageView.setImageDrawable(textDrawable); // Set the Drawable as the image
            }
        }
        private Drawable createTextDrawable(String text) {
            // Define an array of background colors
            int[] backgroundColors = {
                    Color.parseColor("#6aa786"), // Color 1
                    Color.parseColor("#E8BFBF"), // Color 2
                    Color.parseColor("#dd4f8b"), // Color 3
                    Color.parseColor("#fff778"), // Color 4
                    Color.parseColor("#32c4d3"), // Color 5
                    Color.parseColor("#674fa3"), // Color 6
                    Color.parseColor("#91BB91"), // Color 7
                    Color.parseColor("#e8def7"), // Color 8
                    Color.parseColor("#F09AFF"), // Color 9
                    Color.parseColor("#FFAF33"), // Color 10
                    // Add more colors as needed
            };

            // Find the index of the alphabet (A=0, B=1, C=2, etc.) and select the corresponding background color
            int alphabetIndex = Character.toUpperCase(text.charAt(0)) - 'A'; // Assuming you want to support uppercase letters
            int backgroundColor = backgroundColors[alphabetIndex % backgroundColors.length];

            // Create a rounded rectangle shape for the background
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(100); // Adjust the corner radius as needed
            drawable.setColor(backgroundColor);

            // Set the size of the image (adjust as needed)
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, itemView.getContext().getResources().getDisplayMetrics());
            drawable.setSize(size, size);

            // Set text color to white
            int textColor = Color.WHITE;

            // Create a TextView with the contact initial text
            TextView textView = new TextView(itemView.getContext());
            textView.setText(text);
                       textView.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            textView.setTextColor(textColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // Adjust the text size here
            textView.setGravity(Gravity.CENTER);

            // Convert the TextView to a Drawable and set it as the image
            BitmapDrawable bitmapDrawable = (BitmapDrawable) convertViewToDrawable(textView);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawable, bitmapDrawable});
            layerDrawable.setLayerInset(1, 16, 16, 16, 16); // Adjust the position of the text
// Adjust the position of the text (centered)


            return layerDrawable;
        }
        // Helper method to convert a View to a Drawable
        private Drawable convertViewToDrawable(View view) {
            int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(spec, spec);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

            Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            c.translate(-view.getScrollX(), -view.getScrollY());
            view.draw(c);
            view.setDrawingCacheEnabled(true);
            Bitmap cacheBmp = view.getDrawingCache();
            Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
            view.destroyDrawingCache();
            return new BitmapDrawable(itemView.getContext().getResources(), viewBmp);
        }

        private void showContactOptionsDialog(CallLogItem callLogItem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(callLogItem.getName());

            View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout, null);
            builder.setView(dialogView);


            recyclerView = dialogView.findViewById(R.id.callHistoryRecyclerView);

            // Retrieve call history list for the selected number
            List<CallHistoryItem> callHistoryList = getCallHistoryList(callLogItem.getNumber());

            // Set up RecyclerView with CallHistoryAdapter
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            CallHistoryAdapter adapter = new CallHistoryAdapter(context, callHistoryList);
            recyclerView.setAdapter(adapter);

            ImageView callIcon = dialogView.findViewById(R.id.callIcon);
            ImageView textIcon = dialogView.findViewById(R.id.textIcon);
            ImageView deleteIcon = dialogView.findViewById(R.id.deleteIcon);

            callIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = callLogItem.getNumber();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        context.startActivity(callIntent);
                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
                    }
                }
            });

            textIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = callLogItem.getNumber();
                    Intent textIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                    context.startActivity(textIntent);
                }
            });

            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog(callLogItem);
                }
            });

            builder.setPositiveButton("Close", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void showDeleteConfirmationDialog(CallLogItem callLogItem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Call Log");
            builder.setMessage("Are you sure you want to delete this call log entry?");

            builder.setTitle(callLogItem.getName());

            RecyclerView recyclerView = dialogView.findViewById(R.id.callHistoryRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            CallHistoryAdapter adapter = new CallHistoryAdapter(context, getCallHistoryList(callLogItem.getNumber()));
            recyclerView.setAdapter(adapter);
            List<CallHistoryItem> callHistoryList = getCallHistoryList(callLogItem.getNumber());

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteCallLogEntry(callLogItem);
                }
            });

            builder.setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public List<CallHistoryItem> getCallHistoryList(String phoneNumber) {
            List<CallHistoryItem> callHistoryList = new ArrayList<>();

            String[] projection = {
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION
            };

            String selection = CallLog.Calls.NUMBER + " = ?";
            String[] selectionArgs = {phoneNumber};
            String sortOrder = CallLog.Calls.DATE + " DESC";

            Cursor cursor = context.getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                    @SuppressLint("Range") String callDate = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                    @SuppressLint("Range") int callDuration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));

                    String callTypeStr;
                    switch (callType) {
                        case CallLog.Calls.INCOMING_TYPE:
                            callTypeStr = "Incoming";
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            callTypeStr = "Outgoing";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            callTypeStr = "Missed";
                            break;
                        default:
                            callTypeStr = "Unknown";
                    }

                    String formattedDate = formatDate(callDate);
                    String formattedDuration = formatDuration(callDuration);

                    CallHistoryItem item = new CallHistoryItem(callTypeStr, formattedDate, formattedDuration);
                    callHistoryList.add(item);

                } while (cursor.moveToNext());

                cursor.close();
            }

            return callHistoryList;
        }

        private String formatDate(String callDate) {
            long timestamp = Long.parseLong(callDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return dateFormat.format(new Date(timestamp));
        }


        private String formatDuration(int seconds) {
            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;
            return String.format(Locale.getDefault(), "%d:%02d", minutes, remainingSeconds);
        }

    }



        private void deleteCallLogEntry(CallLogItem callLogItem) {
            String selection = CallLog.Calls.NUMBER + " = ? AND " + CallLog.Calls.DATE + " = ?";
            String[] selectionArgs = new String[]{callLogItem.getNumber(), String.valueOf(callLogItem.getTime())};
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, selection, selectionArgs);
                originalCallLogItems.remove(callLogItem);
                notifyDataSetChanged();
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALL_LOG}, CALL_PERMISSION_REQUEST_CODE);
            }
        }
    }




