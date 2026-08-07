package com.idefav.helloworld.weather.domain;

import java.time.Instant;

public class CurrentWeather {
    private final double temperature;
    private final int weatherCode;
    private final Instant observedAt;

    public CurrentWeather(double temperature, int weatherCode, Instant observedAt) {
        this.temperature = temperature;
        this.weatherCode = weatherCode;
        this.observedAt = observedAt;
    }

    public double getTemperature() { return temperature; }
    public int getWeatherCode() { return weatherCode; }
    public Instant getObservedAt() { return observedAt; }
}
