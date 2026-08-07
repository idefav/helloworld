package com.idefav.helloworld.weather.api;

import com.idefav.helloworld.weather.application.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
    private final WeatherService service;
    public WeatherController(WeatherService service) { this.service = service; }

    @GetMapping(value = "/weather", produces = "application/json")
    public WeatherResponse weather(@RequestParam(value = "location", required = false) String location) {
        return service.queryCurrentWeather(location);
    }
}
