package com.idefav.helloworld.weather.infrastructure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(WeatherProperties.class)
public class WeatherConfiguration {
    @Bean
    public RestTemplate weatherRestTemplate(RestTemplateBuilder builder, WeatherProperties properties) {
        return builder.setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout()).build();
    }
}
