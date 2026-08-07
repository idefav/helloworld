package com.idefav.helloworld.weather.domain;

public class WeatherProviderTimeoutException extends RuntimeException {
    public WeatherProviderTimeoutException(Throwable cause) { super("weather provider timeout", cause); }
}
