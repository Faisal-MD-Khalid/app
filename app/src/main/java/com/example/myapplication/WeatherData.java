package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.SerializedName;

public class WeatherData {
    @SerializedName("main")
    private String main;

    @SerializedName("description")
    private String description;

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }
}
