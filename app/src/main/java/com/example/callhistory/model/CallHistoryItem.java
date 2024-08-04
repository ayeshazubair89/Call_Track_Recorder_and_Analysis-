package com.example.callhistory.model;


public class CallHistoryItem {
    private String callType;
    private String formattedTime;
    private String formattedDuration;

    public CallHistoryItem(String callType, String formattedTime, String formattedDuration) {
        this.callType = callType;
        this.formattedTime = formattedTime;
        this.formattedDuration = formattedDuration;
    }

    public String getCallType() {
        return callType;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public String getFormattedDuration() {
        return formattedDuration;
    }
}
