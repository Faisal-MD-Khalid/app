package com.example.myapplication;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("main")
    private MainData main;

    @SerializedName("weather")
    private List<WeatherData> weather;

    public MainData getMain() {
        return main;
    }

    public List<WeatherData> getWeather() {
        return weather;
    }
}
