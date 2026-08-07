package com.idefav.helloworld.weather.domain;

public final class WeatherConditionMapper {
    private WeatherConditionMapper() {}

    public static String describe(int code) {
        switch (code) {
            case 0: return "clear_sky";
            case 1: return "mainly_clear";
            case 2: return "partly_cloudy";
            case 3: return "overcast";
            case 45: case 48: return "fog";
            case 51: case 53: case 55: return "drizzle";
            case 56: case 57: return "freezing_drizzle";
            case 61: case 63: case 65: return "rain";
            case 66: case 67: return "freezing_rain";
            case 71: case 73: case 75: return "snow_fall";
            case 77: return "snow_grains";
            case 80: case 81: case 82: return "rain_showers";
            case 85: case 86: return "snow_showers";
            case 95: return "thunderstorm";
            case 96: case 99: return "thunderstorm_with_hail";
            default: return "unknown";
        }
    }
}
