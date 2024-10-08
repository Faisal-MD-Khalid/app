package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.SerializedName;

public class MainData {
    @SerializedName("temp")
    private double temp;

    @SerializedName("humidity")
    private double humidity; // Add this field

    // Getters and setters for temp and humidity
    public double getTemp() {
        return temp;
    }

    public double getHumidity() {
        return humidity;
    }
}

