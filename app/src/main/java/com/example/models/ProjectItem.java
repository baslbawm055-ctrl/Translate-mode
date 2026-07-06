package com.example.models;

public class ProjectItem {
    private final String name;
    private final String path;
    private final String date;

    public ProjectItem(String name, String path, String date) {
        this.name = name;
        this.path = path;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDate() {
        return date;
    }
}
