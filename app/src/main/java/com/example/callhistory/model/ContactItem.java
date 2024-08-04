package com.example.callhistory.model;
public class ContactItem {
    private String name;
    private String number;
    private int totalIncomingCalls;
    private int totalMissedCalls;
    private int totalOutgoingCalls;

    public ContactItem(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public int getTotalIncomingCalls() {
        return totalIncomingCalls;
    }

    public void setTotalIncomingCalls(int totalIncomingCalls) {
        this.totalIncomingCalls = totalIncomingCalls;
    }

    public int getTotalMissedCalls() {
        return totalMissedCalls;
    }

    public void setTotalMissedCalls(int totalMissedCalls) {
        this.totalMissedCalls = totalMissedCalls;
    }

    public int getTotalOutgoingCalls() {
        return totalOutgoingCalls;
    }

    public void setTotalOutgoingCalls(int totalOutgoingCalls) {
        this.totalOutgoingCalls = totalOutgoingCalls;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
