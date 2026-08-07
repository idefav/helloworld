package com.idefav.helloworld.weather;

import java.time.Instant;
import java.util.Optional;

import com.idefav.helloworld.weather.domain.CurrentWeather;
import com.idefav.helloworld.weather.domain.LocationMatch;
import com.idefav.helloworld.weather.domain.WeatherProvider;
import com.idefav.helloworld.weather.domain.WeatherProviderTimeoutException;
import com.idefav.helloworld.weather.domain.WeatherProviderUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerTests {
    @Autowired private MockMvc mockMvc;
    @MockBean private WeatherProvider provider;

    @Test
    void returnsStructuredWeather() throws Exception {
        when(provider.resolveLocation("Shanghai")).thenReturn(Optional.of(new LocationMatch("Shanghai", "China", 31.22, 121.46)));
        when(provider.getCurrentWeather(31.22, 121.46)).thenReturn(new CurrentWeather(28.4, 1, Instant.parse("2026-08-07T14:30:00Z")));

        mockMvc.perform(get("/weather").param("location", "Shanghai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location.name").value("Shanghai"))
                .andExpect(jsonPath("$.location.country").value("China"))
                .andExpect(jsonPath("$.condition.code").value(1))
                .andExpect(jsonPath("$.condition.description").value("mainly_clear"))
                .andExpect(jsonPath("$.temperature.value").value(28.4))
                .andExpect(jsonPath("$.temperature.unit").value("°C"))
                .andExpect(jsonPath("$.observedAt").value("2026-08-07T14:30:00Z"));
    }

    @Test
    void serializesMissingCountryAsNull() throws Exception {
        when(provider.resolveLocation("X")).thenReturn(Optional.of(new LocationMatch("X", null, 1, 2)));
        when(provider.getCurrentWeather(1, 2)).thenReturn(new CurrentWeather(3, 0, Instant.EPOCH));
        mockMvc.perform(get("/weather").param("location", "X"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.location.country").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void returnsFixed400EnvelopeForInvalidLocations() throws Exception {
        when(provider.resolveLocation("Atlantis")).thenReturn(Optional.empty());
        mockMvc.perform(get("/weather")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_LOCATION"));
        mockMvc.perform(get("/weather").param("location", "   ")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("location is invalid or could not be resolved"));
        mockMvc.perform(get("/weather").param("location", "Atlantis")).andExpect(status().isBadRequest());
    }

    @Test
    void returnsSanitized502And504Responses() throws Exception {
        String secret = "internal.example?api_key=secret-marker";
        when(provider.resolveLocation("Failure")).thenThrow(new WeatherProviderUnavailableException(new RuntimeException(secret)));
        mockMvc.perform(get("/weather").param("location", "Failure"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error.code").value("WEATHER_PROVIDER_UNAVAILABLE"))
                .andExpect(content().string(not(containsString("secret-marker"))))
                .andExpect(content().string(not(containsString("internal.example"))));

        when(provider.resolveLocation("Timeout")).thenThrow(new WeatherProviderTimeoutException(new RuntimeException(secret)));
        mockMvc.perform(get("/weather").param("location", "Timeout"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.error.code").value("WEATHER_PROVIDER_TIMEOUT"))
                .andExpect(content().string(not(containsString("secret-marker"))));
    }
}
