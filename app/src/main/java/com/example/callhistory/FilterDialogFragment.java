package com.example.callhistory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FilterDialogFragment extends DialogFragment {
    Spinner sortBySpinner;

    private String selectedCallType = "All Calls";

    private boolean isAllCallsSelected = false;

    private EditText startDateEditText;
    private EditText endDateEditText;
    private FilterDialogListener listener;
    private Calendar selectedStartTime;
    private Calendar selectedEndTime;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TimePicker startTimePicker;
    private TimePicker endTimePicker;
    EditText minDuration, maxDuration;
    private EditText startTime, endTime;

    private ProgressDialog progressDialog;
    private boolean isIncomingSelected = false;
    private boolean isOutgoingSelected = false;
    private boolean isMissedSelected = false;


    ImageView  closeButton;
    Button incomingCallsButton,outgoingCallsButton ,  missedCallsButton;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FilterDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement FilterDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_layout, container, false);
        String selectedCallType = this.selectedCallType;
// Replace 'this' with 'requireContext()' to get the fragment's context
        progressDialog = new ProgressDialog(requireContext());

        // Initialize UI elements for call type buttons
        Button allCallsButton = rootView.findViewById(R.id.bt_all_calls);
        Button incomingCallsButton = rootView.findViewById(R.id.bt_incoming_calls);
        Button outgoingCallsButton = rootView.findViewById(R.id.bt_outgoing_calls);
        Button missedCallsButton = rootView.findViewById(R.id.bt_missed_calls);
        minDuration = rootView.findViewById(R.id.min_duration_edit_text);
        maxDuration = rootView.findViewById(R.id.max_duration_edit_text);


closeButton = rootView.findViewById(R.id.bt_close);

        // Set an OnClickListener for the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the filter dialog
                dismiss();

                // Show a toast message
                //showToast("Data is unfiltered");
            }
        });

        // Initialize the start and end time EditText fields
        startTime = rootView.findViewById(R.id.time_start_text);
        endTime = rootView.findViewById(R.id.time_end_text);
        startDateEditText = rootView.findViewById(R.id.start_date_edit_text);
        endDateEditText = rootView.findViewById(R.id.end_date_edit_text);

        // Set click listeners for the time EditText fields
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(startTime, selectedStartTime);
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(endTime, selectedEndTime);
            }
        });

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateEditText);
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endDateEditText);
            }
        });

        // Initialize the Spinner
        sortBySpinner = rootView.findViewById(R.id.sort_by_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        sortBySpinner.setAdapter(adapter);
// Declare the selectedCallType as final
        // Declare the selectedCallType as final
        final String finalSelectedCallType = selectedCallType;
    incomingCallsButton = rootView.findViewById(R.id.bt_incoming_calls);
        outgoingCallsButton = rootView.findViewById(R.id.bt_outgoing_calls);
        missedCallsButton = rootView.findViewById(R.id.bt_missed_calls);



        setCallTypeButtonListeners(allCallsButton, "All Calls");
        setCallTypeButtonListeners(incomingCallsButton, "1");
        setCallTypeButtonListeners(outgoingCallsButton, "2");
        setCallTypeButtonListeners(missedCallsButton, "3");

        Button submitButton = rootView.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Collect filter options

                String selectedSortOption = sortBySpinner.getSelectedItem().toString();
                String startDate = startDateEditText.getText().toString();
                String endDate = endDateEditText.getText().toString();
                String selectedMinDuration = minDuration.getText().toString();
                String selectedMaxDuration = maxDuration.getText().toString();
                String selectedStartTime = startTime.getText().toString();
                String selectedEndTime = endTime.getText().toString();


                // Handle date parsing and input validation for date-related fields
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date filterStartDate = null;
                Date filterEndDate = null;

                try {
                    // Parse startDate and endDate strings to Date objects
                    filterStartDate = dateFormat.parse(startDate);
                    filterEndDate = dateFormat.parse(endDate);
                } catch (ParseException e) {
                    // Handle date parsing errors here
                    e.printStackTrace();
                }
                // Show a progress dialog

                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                // Check if filterStartDate and filterEndDate are valid Dates
                if (filterStartDate != null && filterEndDate != null) {
                    // Here, you have valid Date objects for startDate and endDate
                    // You can proceed with filtering based on these dates

                    // Create a Bundle to pass filter options to the parent fragment
                    Bundle filterOptions = new Bundle();
                    filterOptions.putString("sortOption", selectedSortOption);
                    filterOptions.putString("startDate", startDate);
                    filterOptions.putString("endDate", endDate);
                    filterOptions.putString("minDuration", selectedMinDuration);
                    filterOptions.putString("maxDuration", selectedMaxDuration);
                   // filterOptions.putString("selectedCallType", selectedCallType); // Use the updated selectedCallType
                    filterOptions.putString("startTime", selectedStartTime); // Add start time
                    filterOptions.putString("endTime", selectedEndTime);
                    filterOptions.putBoolean("incomingSelected", isIncomingSelected);
                    filterOptions.putBoolean("outgoingSelected", isOutgoingSelected);
                    filterOptions.putBoolean("missedSelected", isMissedSelected);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getTargetFragment() != null && getTargetFragment() instanceof FilterDialogListener) {
                                ((FilterDialogListener) getTargetFragment()).applyFilters(filterOptions);
                            }
                            Log.d("FilterDialogFragment", "Submit button clicked");
                            if (listener != null) {
                                listener.applyFilters(filterOptions);
                            }
                            progressDialog.dismiss();
                        }
                    }, 1000);
                   /* // Call the listener method in the parent fragment to apply filters
                    if (getTargetFragment() != null && getTargetFragment() instanceof FilterDialogListener) {
                        ((FilterDialogListener) getTargetFragment()).applyFilters(filterOptions);
                    }
*/
                   /* Log.d("FilterDialogFragment", "Submit button clicked");
                    if (listener != null) {
                        listener.applyFilters(filterOptions);
                    }*/
                    // Dismiss the dialog
                    dismiss();
                } else {
                    // Handle invalid dates here
                    dismiss();
                    // You can display an error message or take appropriate action
                    Log.d("FilterDialogFragment", "Invalid data");
                }
            }
        });

        return rootView;
    }

  private void setCallTypeButtonListeners(Button button, final String callType) {
      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              // Toggle the selection state of the button
              if (callType.equals("1")) {
                  isIncomingSelected = !isIncomingSelected;
              } else if (callType.equals("2")) {
                  isOutgoingSelected = !isOutgoingSelected;
              } else if (callType.equals("3")) {
                  isMissedSelected = !isMissedSelected;
              }

              // Update the UI for the clicked button
              updateCallTypeButtonUI(button, isIncomingSelected, isOutgoingSelected, isMissedSelected);

              // Perform any other actions you need based on the selection state
          }
      });
  }

    // Helper method to update the UI state of call type buttons
    private void updateCallTypeButtonUI(Button button, boolean isIncomingSelected, boolean isOutgoingSelected, boolean isMissedSelected) {
        // Set the default background color
        int backgroundColor = getResources().getColor(R.color.dim_purple);

        // Determine the appropriate background color based on the selection state
        if (isIncomingSelected && button.getTag() != null && button.getTag().equals("1")) {
            backgroundColor = getResources().getColor(R.color.dark_purple);
        } else if (isOutgoingSelected && button.getTag() != null && button.getTag().equals("2")) {
            backgroundColor = getResources().getColor(R.color.dark_purple);
        } else if (isMissedSelected && button.getTag() != null && button.getTag().equals("3")) {
            backgroundColor = getResources().getColor(R.color.dark_purple);
        }

        // Update the background color of the button
        button.setBackgroundColor(backgroundColor);
    }

    // Helper method to update the UI state of a call type button
    private void updateCallTypeButtonUI(Button button, boolean isSelected) {
        if (button != null) {
            button.setBackgroundColor(getResources().getColor(R.color.dim_purple));
        } else {
            Log.e("FilterDebug", "Button is null");
        }
    }
        private void showTimePickerDialog(final EditText durationEditText) {
        // Get the context from the EditText view
        Context context = durationEditText.getContext();

        // Get the current values for hours, minutes, and seconds from the EditText
        String currentValue = durationEditText.getText().toString();
        String[] parts = currentValue.split(":");
        int currentHours = 0;
        int currentMinutes = 0;
        int currentSeconds = 0;

        if (parts.length == 3) {
            currentHours = Integer.parseInt(parts[0]);
            currentMinutes = Integer.parseInt(parts[1]);
            currentSeconds = Integer.parseInt(parts[2]);
        }

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context, // Use the obtained context
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hours, int minutes) {
                        // When the user sets a time, update the EditText with the selected duration
                        durationEditText.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, 0));
                    }
                },
                currentHours, // Initial hours value
                currentMinutes, // Initial minutes value
                true // 24-hour format
        );

        timePickerDialog.show();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    private void showTimePickerDialog(final EditText timeEditText, final Calendar selectedTime) {
        // Initialize a new Calendar instance for the end time
        Calendar endTimeCalendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Update the endTimeCalendar
                endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTimeCalendar.set(Calendar.MINUTE, minute);

                // Update the EditText with the selected time
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
                timeEditText.setText(timeFormat.format(endTimeCalendar.getTime()));
            }
        };

        // Get the current time as the default
        int hour = selectedTime != null ? selectedTime.get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = selectedTime != null ? selectedTime.get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                timeSetListener,
                hour,
                minute,
                android.text.format.DateFormat.is24HourFormat(requireContext())
        );

        // Show the dialog
        timePickerDialog.show();
    }
    // Inside setCallTypeButtonListeners method

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // Handle dialog dismissal if needed
        Log.d("FilterDialogFragment", "Dialog dismissed");
        // Add your custom logic here
    }



    private void showDatePickerDialog(EditText dateEditText) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Update the EditText with the selected date
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                dateEditText.setText(dateFormat.format(calendar.getTime()));
            }
        };

        // Get the current date as the default
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), dateSetListener, year, month, day);

        // Show the dialog
        datePickerDialog.show();
    }

    public interface FilterDialogListener {
        void applyFilters(Bundle filterOptions);
    }
}
