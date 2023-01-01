package com.idefav.helloworld;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/say/{name}")
    public String say(@PathVariable(name = "name") String name) {
        return String.format("hello v1,%s", name);
    }
}
