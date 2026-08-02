package com.idefav.helloworld;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeController {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @GetMapping(value = "/time", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> time() {
        Instant now = Instant.now();
        return Collections.singletonMap("time", TIME_FORMATTER.format(now));
    }
}
