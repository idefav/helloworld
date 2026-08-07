package com.idefav.helloworld.weather.infrastructure;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.provider")
public class WeatherProperties {
    private String geocodingBaseUrl;
    private String forecastBaseUrl;
    private Duration connectTimeout;
    private Duration readTimeout;
    public String getGeocodingBaseUrl() { return geocodingBaseUrl; }
    public void setGeocodingBaseUrl(String value) { this.geocodingBaseUrl = value; }
    public String getForecastBaseUrl() { return forecastBaseUrl; }
    public void setForecastBaseUrl(String value) { this.forecastBaseUrl = value; }
    public Duration getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(Duration value) { this.connectTimeout = value; }
    public Duration getReadTimeout() { return readTimeout; }
    public void setReadTimeout(Duration value) { this.readTimeout = value; }
}
