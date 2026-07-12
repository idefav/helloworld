package com.idefav.helloworld;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @GetMapping(value = "/versionz", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> version() {
        Map<String, String> version = new LinkedHashMap<>();
        version.put("service", "helloworld");
        version.put("version", "0.0.1-SNAPSHOT");
        return version;
    }
}
