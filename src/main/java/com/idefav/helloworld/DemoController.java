package com.idefav.helloworld;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> demo() {
        return Collections.singletonMap("message", "Hello, World!");
    }

    @GetMapping("/say/{name}")
    public String say(@PathVariable(name = "name") String name) {
        return String.format("hello v2,%s", name);
    }
}
