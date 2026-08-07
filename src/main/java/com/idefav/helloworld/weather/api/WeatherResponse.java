package com.idefav.helloworld.weather.api;

public class WeatherResponse {
    private final Location location;
    private final Condition condition;
    private final Temperature temperature;
    private final String observedAt;

    public WeatherResponse(Location location, Condition condition, Temperature temperature, String observedAt) {
        this.location = location;
        this.condition = condition;
        this.temperature = temperature;
        this.observedAt = observedAt;
    }

    public Location getLocation() { return location; }
    public Condition getCondition() { return condition; }
    public Temperature getTemperature() { return temperature; }
    public String getObservedAt() { return observedAt; }

    public static class Location {
        private final String name;
        private final String country;
        private final double latitude;
        private final double longitude;
        public Location(String name, String country, double latitude, double longitude) {
            this.name = name; this.country = country; this.latitude = latitude; this.longitude = longitude;
        }
        public String getName() { return name; }
        public String getCountry() { return country; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }

    public static class Condition {
        private final int code;
        private final String description;
        public Condition(int code, String description) { this.code = code; this.description = description; }
        public int getCode() { return code; }
        public String getDescription() { return description; }
    }

    public static class Temperature {
        private final double value;
        private final String unit;
        public Temperature(double value, String unit) { this.value = value; this.unit = unit; }
        public double getValue() { return value; }
        public String getUnit() { return unit; }
    }
}
