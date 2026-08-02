package com.idefav.helloworld;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeController {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
                    .withZone(ZoneOffset.UTC);

    @GetMapping(value = "/time", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> time() {
        Instant now = Instant.now();
        return Collections.singletonMap("time", TIME_FORMATTER.format(now));
    }
}
