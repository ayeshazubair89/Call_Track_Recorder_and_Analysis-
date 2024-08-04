package com.example.callhistory.model;






public class FilteredCallLogItems {
    private String getCallHistory(String phoneNumber) {
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

        public  FilteredCallLogItems (String number) {
            this.number = number;
        }

        public  FilteredCallLogItems () {

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
    private boolean isHeader; // Add this field
    public void setHeader(boolean header) {
        isHeader = header;
    }
    public boolean isHeader() {
        return isHeader;
    }
        public String getCallType() {
            return callType;
        }

        public void setCallType(String callType) {
            this.callType = callType;
        }
    }

