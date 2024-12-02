package com.example.todo.service.api.http;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.todo.config.http.HttpConfiguration;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ApiHttpManager {
    
    private HttpConfiguration httpConfiguration;
    private ApiHttpClient apiHttpClient;

    public Response getTodos(Map<String, Object> queryParams) {
        return apiHttpClient.getRequest(
            httpConfiguration.getApiBaseUrl(),
            "/todos", 
            queryParams
        );
    }

    public Response createTodos(Object body) {
        return apiHttpClient.postRequest(
            httpConfiguration.getApiBaseUrl(),
            "/todos",
            body
        );
    }

    public Response updateTodos(String todoId, Object body) {
        return apiHttpClient.putRequest(
            httpConfiguration.getApiBaseUrl(),
            "/todos",
            todoId,
            body
        );
    }

    public Response deleteTodos(String nameAuth, String passwordAuth, String todoId) {
        return apiHttpClient.deleteRequest(
            nameAuth,
            passwordAuth,
            httpConfiguration.getApiBaseUrl(),
            "/todos",
            todoId
        );
    }
    
}
