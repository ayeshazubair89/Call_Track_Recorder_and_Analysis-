package com.example.callhistory.model;

public class CallLogStatics {

    private int totalCalls;
    private int totalMissedCalls;
    private int totalOutgoingCalls;
    private int totalIncomingCalls;
    private int totalRejectedCalls;
    private String frequentCaller;
    private int maxCallDuration;
    private String longestCaller;

    public CallLogStatics(int totalCalls, int totalMissedCalls, int totalOutgoingCalls, int totalIncomingCalls, int totalRejectedCalls, String frequentCaller, int maxCallDuration, String longestCaller) {
        this.totalCalls = totalCalls;
        this.totalMissedCalls = totalMissedCalls;
        this.totalOutgoingCalls = totalOutgoingCalls;
        this.totalIncomingCalls = totalIncomingCalls;
        this.totalRejectedCalls = totalRejectedCalls;
        this.frequentCaller = frequentCaller;
        this.maxCallDuration = maxCallDuration;
        this.longestCaller = longestCaller;
    }

    public int getTotalCalls() {
        return totalCalls;
    }

    public int getTotalMissedCalls() {
        return totalMissedCalls;
    }

    public int getTotalOutgoingCalls() {
        return totalOutgoingCalls;
    }

    public int getTotalIncomingCalls() {
        return totalIncomingCalls;
    }

    public int getTotalRejectedCalls() {
        return totalRejectedCalls;
    }

    public String getFrequentCaller() {
        return frequentCaller;
    }

    public int getMaxCallDuration() {
        return maxCallDuration;
    }

    public String getLongestCaller() {
        return longestCaller;
    }
}