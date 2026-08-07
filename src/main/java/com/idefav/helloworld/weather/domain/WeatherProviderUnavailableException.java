package com.idefav.helloworld.weather.domain;

public class WeatherProviderUnavailableException extends RuntimeException {
    public WeatherProviderUnavailableException() { super("weather provider unavailable"); }
    public WeatherProviderUnavailableException(Throwable cause) { super("weather provider unavailable", cause); }
}
