package com.example.models;

public class HistoryItem {
    private final String action;
    private final String fileDetails;
    private final String date;

    public HistoryItem(String action, String fileDetails, String date) {
        this.action = action;
        this.fileDetails = fileDetails;
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public String getFileDetails() {
        return fileDetails;
    }

    public String getDate() {
        return date;
    }
}
