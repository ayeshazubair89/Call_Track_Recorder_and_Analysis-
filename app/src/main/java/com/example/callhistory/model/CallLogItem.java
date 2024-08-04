package com.example.callhistory.model;


import android.util.Log;

public class CallLogItem {
    public String getCallHistory(String phoneNumber) {
        // Implement code to retrieve call history for the given phoneNumber
        // This can involve querying the call log, database, or other data sources.
        // Return a formatted call history string.
        String callHistory = "Call history for " + phoneNumber + ":\n";
        // Add call details to the callHistory string.
        return callHistory;
    }

    private String name;
    private String number;
    private String date;
    private String time;
    private String callType;
    private String duration;
    private boolean isFiltered; // Add this field

    public boolean isFiltered() {
        return isFiltered;
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }

    public CallLogItem(String number) {
        this.number = number;
    }
    private boolean isHeader; // Add this field
    public void setHeader(boolean header) {
        isHeader = header;
    }
    public boolean isHeader() {
        return isHeader;
    }
    public CallLogItem() {
      //  Log.d("CallLogItem", "Date: " + getDate());
      //  Log.d("CallLogItem", "Duration: " + getDuration());
    }

    // Add getters and setters for the new 'time' field

    private String callTime;

    // Constructor and other methods

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    // Add getters and setters for the new 'duration' and 'time' fields
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
   /* public String getCallType() {
        return callType;
    }*/
    public String getCallType() {
        // Return the call type for this item (e.g., "Incoming" or "Outgoing")
        return callType;
    }
    public void setCallType(String callType) {
        this.callType = callType;
    }
    private int arrowColorResId;

    public int getArrowColorResId() {
        return arrowColorResId;
    }
    public CallLogItem(String date, boolean isHeader) {
        this.date = date;
        this.isHeader = isHeader;
    }
    public void setArrowColorResId(int arrowColorResId) {
        this.arrowColorResId = arrowColorResId;
    }


    public String getPhoneNumber() {
        // Logic to retrieve the phone number
        String phoneNumber = "1234567890"; // Replace this with actual logic to get the phone number
        return phoneNumber;
    }

}