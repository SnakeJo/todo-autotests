package com.example.todo.service.api.ws;

import java.net.URI;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import com.example.todo.config.ws.WebSocketConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiWsClient extends WebSocketClient {

    private Consumer<String> messageHandler;
    
    public ApiWsClient(WebSocketConfiguration webSocketConfiguration) throws Exception {
        super(new URI(webSocketConfiguration.getApiWsBaseUrl()));
    }

    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("WebSocket connection opened");
        log.info("HTTP Status: {}", handshakedata.getHttpStatus());
        log.info("HTTP Status Message: {}", handshakedata.getHttpStatusMessage());
    }

    public void onMessage(String message) {
        log.info("Received message: {}", message);
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("WebSocket closed. Code: {}, Reason: {}, Remote: {}", code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        log.error("WebSocket error occurred: {}", ex.getMessage(), ex);
    }
}