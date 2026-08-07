package com.idefav.helloworld.weather.infrastructure;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.idefav.helloworld.weather.domain.CurrentWeather;
import com.idefav.helloworld.weather.domain.LocationMatch;
import com.idefav.helloworld.weather.domain.WeatherProvider;
import com.idefav.helloworld.weather.domain.WeatherProviderTimeoutException;
import com.idefav.helloworld.weather.domain.WeatherProviderUnavailableException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OpenMeteoWeatherProvider implements WeatherProvider {
    private final RestTemplate restTemplate;
    private final WeatherProperties properties;

    public OpenMeteoWeatherProvider(RestTemplate weatherRestTemplate, WeatherProperties properties) {
        this.restTemplate = weatherRestTemplate;
        this.properties = properties;
    }

    @Override
    public Optional<LocationMatch> resolveLocation(String location) {
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getGeocodingBaseUrl()).path("/v1/search")
                .queryParam("name", location).queryParam("count", 1).queryParam("language", "en")
                .queryParam("format", "json").build().encode().toUri();
        GeocodingResponse response = get(uri, GeocodingResponse.class);
        if (response == null || response.results == null || response.results.isEmpty()) return Optional.empty();
        GeocodingResult result = response.results.get(0);
        if (result == null || isBlank(result.name) || result.latitude == null || result.longitude == null
                || !validCoordinates(result.latitude, result.longitude)) throw new WeatherProviderUnavailableException();
        return Optional.of(new LocationMatch(result.name, result.country, result.latitude, result.longitude));
    }

    @Override
    public CurrentWeather getCurrentWeather(double latitude, double longitude) {
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getForecastBaseUrl()).path("/v1/forecast")
                .queryParam("latitude", latitude).queryParam("longitude", longitude)
                .queryParam("current", "temperature_2m,weather_code").queryParam("temperature_unit", "celsius")
                .queryParam("timezone", "GMT").queryParam("timeformat", "iso8601").build().encode().toUri();
        ForecastResponse response = get(uri, ForecastResponse.class);
        Current current = response == null ? null : response.current;
        if (current == null || current.temperature == null || current.weatherCode == null || isBlank(current.time)
                || !Double.isFinite(current.temperature)) throw new WeatherProviderUnavailableException();
        try {
            Instant instant = LocalDateTime.parse(current.time).toInstant(ZoneOffset.UTC);
            return new CurrentWeather(current.temperature, current.weatherCode, instant);
        } catch (DateTimeParseException ex) {
            throw new WeatherProviderUnavailableException(ex);
        }
    }

    private <T> T get(URI uri, Class<T> type) {
        try {
            return restTemplate.getForObject(uri, type);
        } catch (ResourceAccessException ex) {
            Throwable cause = NestedExceptionUtils.getMostSpecificCause(ex);
            if (cause instanceof SocketTimeoutException) throw new WeatherProviderTimeoutException(ex);
            throw new WeatherProviderUnavailableException(ex);
        } catch (RestClientException ex) {
            throw new WeatherProviderUnavailableException(ex);
        }
    }

    private boolean validCoordinates(double latitude, double longitude) {
        return Double.isFinite(latitude) && Double.isFinite(longitude)
                && latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }
    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }

    static class GeocodingResponse { public List<GeocodingResult> results; }
    static class GeocodingResult {
        public String name;
        public String country;
        public Double latitude;
        public Double longitude;
    }
    static class ForecastResponse { public Current current; }
    static class Current {
        @JsonProperty("temperature_2m") public Double temperature;
        @JsonProperty("weather_code") public Integer weatherCode;
        public String time;
    }
}
