package com.example.callhistory.model;

public class CustomContactItem {
    private String name;
    private int callCount;

    public CustomContactItem(String name, int callCount) {
        this.name = name;
        this.callCount = callCount;
    }



    public String getName() {
        return name;
    }

    public int getCallCount() {
        return callCount;
    }
}

