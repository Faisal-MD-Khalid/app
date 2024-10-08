package com.example.myapplication;

import java.util.HashMap;
import java.util.Map;

public class VideoItem {
    private String videoKey; // Firebase key for the video item
    private String title;
    private String vurl;
    private int likeCount;
    private int dislikeCount;
    private Map<String, Boolean> likes;
    private Map<String, Boolean> dislikes;

    // Constructors, getters, and setters
    // Constructor
    public VideoItem() {
        // Default constructor required for Firebase
        this.likes = new HashMap<>();
        this.dislikes = new HashMap<>();
    }

    // Getters and setters
    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVurl() {
        return vurl;
    }

    public void setVurl(String vurl) {
        this.vurl = vurl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public Map<String, Boolean> getLikes() {
        if (likes == null) {
            likes = new HashMap<>();
        }
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, Boolean> getDislikes() {
        if (dislikes == null) {
            dislikes = new HashMap<>();
        }
        return dislikes;
    }

    public void setDislikes(Map<String, Boolean> dislikes) {
        this.dislikes = dislikes;
    }
}
