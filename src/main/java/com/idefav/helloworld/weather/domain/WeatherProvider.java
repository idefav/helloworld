package com.idefav.helloworld.weather.domain;

import java.util.Optional;

public interface WeatherProvider {
    Optional<LocationMatch> resolveLocation(String location);
    CurrentWeather getCurrentWeather(double latitude, double longitude);
}
