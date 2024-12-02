package com.example.todo.config.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class WebSocketConfiguration {
    
    @Value("${api.ws.base.url}")
    private String apiWsBaseUrl;
}
