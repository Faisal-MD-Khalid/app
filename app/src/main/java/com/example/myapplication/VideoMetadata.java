package com.example.myapplication;
public class VideoMetadata {
    private String filename;
    private String url;
    private String title;

    public VideoMetadata(String filename) {
        // Default constructor required for Firestore
    }

    public VideoMetadata(String filename, String url, String title) {
        this.filename = filename;
        this.url = url;
        this.title = title;
    }

    // Getter and setter for filename
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    // Getter and setter for url
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Getter and setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
