package com.example.myapplication;

import java.util.Map;

public class filemodel {
    private String title;
    private String vurl;
    private int likeCount;
    private int dislikeCount;
    private Map<String, Boolean> likes;
    private Map<String, Boolean> dislikes;

    public filemodel(String title, String vurl) {
        this.title = title;
        this.vurl = vurl;
        // Default constructor required for Firebase
    }

    public filemodel(String title, String vurl, int likeCount, int dislikeCount, Map<String, Boolean> likes, Map<String, Boolean> dislikes) {
        this.title = title;
        this.vurl = vurl;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    // Getters and setters for all fields
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
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, Boolean> getDislikes() {
        return dislikes;
    }

    public void setDislikes(Map<String, Boolean> dislikes) {
        this.dislikes = dislikes;
    }
}
