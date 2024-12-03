package com.example.todo.ws;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.example.todo.utils.DataHelper.generateRandomBoolean;
import static com.example.todo.utils.DataHelper.generateRandomLong;
import static com.example.todo.utils.DataHelper.generateTodoText;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.todo.TestsConfiguration;
import com.example.todo.config.ws.WebSocketConfiguration;
import com.example.todo.service.api.ws.ApiWsManager;

@Tags({
    @Tag("ws"),
    @Tag("todos")})
public class WebSocketTodoTests extends TestsConfiguration{

    @Autowired
    protected WebSocketConfiguration webSocketConfiguration;

    private ApiWsManager apiWsManager;

    @BeforeEach
    void createWsManager() throws Exception {
        apiWsManager = new ApiWsManager(webSocketConfiguration);
    }
    
    @Test
    @DisplayName("Test WebSocket connection")
    void testWebSocketConnection() throws Exception {
        
        step("connect ws", () -> apiWsManager.connectBlocking());

        step("check ws connection is open", () -> 
            assertTrue(apiWsManager.isOpen(), "WebSocket connection was not established")
        );

        step("close ws", () -> apiWsManager.close());
    }

    @Test
    @DisplayName("Test receive update on new task")
    void testReceiveUpdateOnNewTask() throws Exception {
        
        CountDownLatch latch = new CountDownLatch(1);

        step("set message handler", () -> 
                apiWsManager.setMessageHandler(message -> {
                    System.out.println("Received message: " + message);
                    latch.countDown();
            })  
        );

        step("connect ws", () -> apiWsManager.connectBlocking());
        
        var body = step("create todo body", ()-> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), generateRandomBoolean())
        ); 
    
        step("send POST /todos and get response", () -> apiHttpManager.createTodos(body));

        var messageReceived = step("wait for message", () -> latch.await(5, TimeUnit.SECONDS));

        step("close ws", () -> apiWsManager.close());
        
        step("check message received", () -> 
            assertTrue(messageReceived, "WebSocket did not receive the expected update")
        );
    }
}
