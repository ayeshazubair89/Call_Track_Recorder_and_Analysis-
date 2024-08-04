package com.example.callhistory.model;

public class CallHistoryItem2 {
    private String date;
    private String duration;
    private String type;

    public CallHistoryItem2(String date, String duration, String type) {
        this.date = date;
        this.duration = duration;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }
}
