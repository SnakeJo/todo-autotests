package com.example.todo.config.http;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class HttpConfiguration {
    
    @Value("${api.http.base.url}")
    private String apiBaseUrl;
}
