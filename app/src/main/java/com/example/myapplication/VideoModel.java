package com.example.myapplication;

import java.util.List;

public class VideoModel {
    private String title;
    private String videoUrl;
    private int likeCount;
    private int dislikeCount;
    private String videoId;
    private List<String> likedUsers;
    private List<String> dislikedUsers;

    public VideoModel(String title, String videoUrl, int likeCount, int dislikeCount, String videoId, List<String> likedUsers, List<String> dislikedUsers) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.videoId = videoId;
        this.likedUsers = likedUsers;
        this.dislikedUsers = dislikedUsers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public List<String> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(List<String> likedUsers) {
        this.likedUsers = likedUsers;
    }

    public List<String> getDislikedUsers() {
        return dislikedUsers;
    }

    public void setDislikedUsers(List<String> dislikedUsers) {
        this.dislikedUsers = dislikedUsers;
    }
}
