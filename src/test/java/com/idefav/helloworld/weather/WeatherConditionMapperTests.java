package com.idefav.helloworld.weather;

import com.idefav.helloworld.weather.domain.WeatherConditionMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherConditionMapperTests {
    @Test
    void mapsAllWmoGroupsAndUnknownCodes() {
        assertEquals("clear_sky", WeatherConditionMapper.describe(0));
        assertEquals("overcast", WeatherConditionMapper.describe(3));
        assertEquals("fog", WeatherConditionMapper.describe(48));
        assertEquals("drizzle", WeatherConditionMapper.describe(55));
        assertEquals("freezing_drizzle", WeatherConditionMapper.describe(57));
        assertEquals("rain", WeatherConditionMapper.describe(65));
        assertEquals("freezing_rain", WeatherConditionMapper.describe(67));
        assertEquals("snow_fall", WeatherConditionMapper.describe(75));
        assertEquals("snow_grains", WeatherConditionMapper.describe(77));
        assertEquals("rain_showers", WeatherConditionMapper.describe(82));
        assertEquals("snow_showers", WeatherConditionMapper.describe(86));
        assertEquals("thunderstorm", WeatherConditionMapper.describe(95));
        assertEquals("thunderstorm_with_hail", WeatherConditionMapper.describe(99));
        assertEquals("unknown", WeatherConditionMapper.describe(-1));
    }
}
