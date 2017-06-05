package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public AQI aqi;
    public Basic basic;
    public Now now;
    public Suggestion suggestion;
    public String status;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
