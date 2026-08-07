package com.idefav.helloworld.weather.application;

import java.time.format.DateTimeFormatter;

import com.idefav.helloworld.weather.api.WeatherResponse;
import com.idefav.helloworld.weather.domain.CurrentWeather;
import com.idefav.helloworld.weather.domain.LocationMatch;
import com.idefav.helloworld.weather.domain.WeatherConditionMapper;
import com.idefav.helloworld.weather.domain.WeatherProvider;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    private final WeatherProvider provider;

    public WeatherService(WeatherProvider provider) { this.provider = provider; }

    public WeatherResponse queryCurrentWeather(String rawLocation) {
        if (rawLocation == null) throw new InvalidLocationException();
        String location = rawLocation.trim();
        if (location.isEmpty() || location.length() > 100) throw new InvalidLocationException();

        LocationMatch match = provider.resolveLocation(location).orElseThrow(InvalidLocationException::new);
        CurrentWeather current = provider.getCurrentWeather(match.getLatitude(), match.getLongitude());
        return new WeatherResponse(
                new WeatherResponse.Location(match.getName(), match.getCountry(), match.getLatitude(), match.getLongitude()),
                new WeatherResponse.Condition(current.getWeatherCode(), WeatherConditionMapper.describe(current.getWeatherCode())),
                new WeatherResponse.Temperature(current.getTemperature(), "°C"),
                DateTimeFormatter.ISO_INSTANT.format(current.getObservedAt()));
    }
}
