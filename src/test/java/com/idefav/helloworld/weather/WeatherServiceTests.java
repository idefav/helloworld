package com.idefav.helloworld.weather;

import java.time.Instant;
import java.util.Optional;

import com.idefav.helloworld.weather.api.WeatherResponse;
import com.idefav.helloworld.weather.application.InvalidLocationException;
import com.idefav.helloworld.weather.application.WeatherService;
import com.idefav.helloworld.weather.domain.CurrentWeather;
import com.idefav.helloworld.weather.domain.LocationMatch;
import com.idefav.helloworld.weather.domain.WeatherProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class WeatherServiceTests {
    private final WeatherProvider provider = mock(WeatherProvider.class);
    private final WeatherService service = new WeatherService(provider);

    @Test
    void trimsLocationAndMapsSuccessfulResponseIncludingNullCountry() {
        LocationMatch location = new LocationMatch("上海", null, 31.22, 121.46);
        when(provider.resolveLocation("上海")).thenReturn(Optional.of(location));
        when(provider.getCurrentWeather(31.22, 121.46))
                .thenReturn(new CurrentWeather(28.4, 1, Instant.parse("2026-08-07T14:30:00Z")));

        WeatherResponse response = service.queryCurrentWeather("  上海  ");

        assertEquals("上海", response.getLocation().getName());
        assertNull(response.getLocation().getCountry());
        assertEquals("mainly_clear", response.getCondition().getDescription());
        assertEquals(28.4, response.getTemperature().getValue());
        assertEquals("°C", response.getTemperature().getUnit());
        assertEquals("2026-08-07T14:30:00Z", response.getObservedAt());
    }

    @Test
    void rejectsMissingBlankAndOverlongLocationsWithoutCallingProvider() {
        assertThrows(InvalidLocationException.class, () -> service.queryCurrentWeather(null));
        assertThrows(InvalidLocationException.class, () -> service.queryCurrentWeather(" \t "));
        assertThrows(InvalidLocationException.class, () -> service.queryCurrentWeather(repeat('a', 101)));
        verifyNoInteractions(provider);
    }

    @Test
    void rejectsUnresolvedLocationWithoutFetchingWeather() {
        when(provider.resolveLocation("Atlantis")).thenReturn(Optional.empty());
        assertThrows(InvalidLocationException.class, () -> service.queryCurrentWeather("Atlantis"));
        verify(provider, never()).getCurrentWeather(org.mockito.ArgumentMatchers.anyDouble(), org.mockito.ArgumentMatchers.anyDouble());
    }

    @Test
    void mapsUnknownWeatherCodeWithoutDiscardingIt() {
        when(provider.resolveLocation("X")).thenReturn(Optional.of(new LocationMatch("X", "Y", 1, 2)));
        when(provider.getCurrentWeather(1, 2)).thenReturn(new CurrentWeather(3, 123, Instant.EPOCH));
        WeatherResponse response = service.queryCurrentWeather("X");
        assertEquals(123, response.getCondition().getCode());
        assertEquals("unknown", response.getCondition().getDescription());
    }

    private String repeat(char value, int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) result.append(value);
        return result.toString();
    }
}
