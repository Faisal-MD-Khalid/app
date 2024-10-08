package com.example.myapplication;
public class Video {
    private String title;
    private String filename;

    public Video(String title, String filename) {
        this.title = title;
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    // Getter for filename
    public String getFilename() {
        return filename;
    }

    // Setters if needed
}
