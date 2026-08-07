package com.idefav.helloworld.weather.api;

import com.idefav.helloworld.weather.application.InvalidLocationException;
import com.idefav.helloworld.weather.domain.WeatherProviderTimeoutException;
import com.idefav.helloworld.weather.domain.WeatherProviderUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WeatherExceptionHandler {
    @ExceptionHandler(InvalidLocationException.class)
    public ResponseEntity<ApiErrorResponse> invalidLocation() {
        return response(HttpStatus.BAD_REQUEST, "INVALID_LOCATION", "location is invalid or could not be resolved");
    }

    @ExceptionHandler(WeatherProviderTimeoutException.class)
    public ResponseEntity<ApiErrorResponse> providerTimeout() {
        return response(HttpStatus.GATEWAY_TIMEOUT, "WEATHER_PROVIDER_TIMEOUT", "weather provider timed out");
    }

    @ExceptionHandler(WeatherProviderUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> providerUnavailable() {
        return response(HttpStatus.BAD_GATEWAY, "WEATHER_PROVIDER_UNAVAILABLE", "weather provider is unavailable");
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(code, message));
    }
}
