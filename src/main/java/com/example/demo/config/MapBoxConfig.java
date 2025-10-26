package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapBoxConfig {
    @Value("${mapbox.api.key}")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}
