package com.idefav.helloworld.weather.infrastructure;

import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.Optional;

import com.idefav.helloworld.weather.domain.CurrentWeather;
import com.idefav.helloworld.weather.domain.LocationMatch;
import com.idefav.helloworld.weather.domain.WeatherProviderTimeoutException;
import com.idefav.helloworld.weather.domain.WeatherProviderUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class OpenMeteoWeatherProviderTests {
    private MockRestServiceServer server;
    private OpenMeteoWeatherProvider provider;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        WeatherProperties properties = new WeatherProperties();
        properties.setGeocodingBaseUrl("https://geo.test");
        properties.setForecastBaseUrl("https://forecast.test");
        provider = new OpenMeteoWeatherProvider(restTemplate, properties);
    }

    @Test
    void resolvesEncodedLocationUsingCompleteQuery() {
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("https://geo.test/v1/search?")))
                .andExpect(method(HttpMethod.GET)).andExpect(queryParam("name", "%E4%B8%8A%E6%B5%B7%20%E5%B8%82"))
                .andExpect(queryParam("count", "1")).andExpect(queryParam("language", "en"))
                .andExpect(queryParam("format", "json"))
                .andRespond(withSuccess("{\"results\":[{\"name\":\"Shanghai\",\"country\":\"China\",\"latitude\":31.22,\"longitude\":121.46}]}", MediaType.APPLICATION_JSON));

        LocationMatch result = provider.resolveLocation("上海 市").get();
        assertEquals("Shanghai", result.getName());
        assertEquals(31.22, result.getLatitude());
        server.verify();
    }

    @Test
    void returnsEmptyOnlyForEmptyResultsAndRejectsInvalidCoordinates() {
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("https://geo.test/v1/search?")))
                .andRespond(withSuccess("{\"results\":[]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("https://geo.test/v1/search?")))
                .andRespond(withSuccess("{\"results\":[{\"name\":\"Bad\",\"latitude\":91,\"longitude\":0}]}", MediaType.APPLICATION_JSON));
        Optional<LocationMatch> empty = provider.resolveLocation("Nowhere");
        assertFalse(empty.isPresent());
        assertThrows(WeatherProviderUnavailableException.class, () -> provider.resolveLocation("Bad"));
        server.verify();
    }

    @Test
    void fetchesForecastAndNormalizesGmtTimeToInstant() {
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("https://forecast.test/v1/forecast?")))
                .andExpect(queryParam("latitude", "31.22")).andExpect(queryParam("longitude", "121.46"))
                .andExpect(queryParam("current", "temperature_2m,weather_code"))
                .andExpect(queryParam("temperature_unit", "celsius")).andExpect(queryParam("timezone", "GMT"))
                .andExpect(queryParam("timeformat", "iso8601"))
                .andRespond(withSuccess("{\"current\":{\"temperature_2m\":28.4,\"weather_code\":1,\"time\":\"2026-08-07T14:30\"}}", MediaType.APPLICATION_JSON));
        CurrentWeather result = provider.getCurrentWeather(31.22, 121.46);
        assertEquals(Instant.parse("2026-08-07T14:30:00Z"), result.getObservedAt());
        server.verify();
    }

    @Test
    void classifiesProtocolHttpAndTimeoutFailures() {
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("https://forecast.test/v1/forecast?")))
                .andRespond(withSuccess("{\"current\":{\"temperature_2m\":28.4}}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("https://geo.test/v1/search?")))
                .andRespond(withStatus(HttpStatus.BAD_GATEWAY));
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("https://geo.test/v1/search?")))
                .andRespond(request -> { throw new ResourceAccessException("redacted", new SocketTimeoutException("timeout")); });
        assertThrows(WeatherProviderUnavailableException.class, () -> provider.getCurrentWeather(1, 2));
        assertThrows(WeatherProviderUnavailableException.class, () -> provider.resolveLocation("X"));
        assertThrows(WeatherProviderTimeoutException.class, () -> provider.resolveLocation("Y"));
        server.verify();
    }

    @Test
    void classifiesMalformedJsonAndOtherIoAsUnavailable() {
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("https://geo.test/v1/search?")))
                .andRespond(withSuccess("{not-json", MediaType.APPLICATION_JSON));
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("https://geo.test/v1/search?")))
                .andRespond(request -> { throw new ResourceAccessException("redacted", new java.io.IOException("connection refused")); });

        assertThrows(WeatherProviderUnavailableException.class, () -> provider.resolveLocation("Broken"));
        assertThrows(WeatherProviderUnavailableException.class, () -> provider.resolveLocation("Offline"));
        server.verify();
    }
}
