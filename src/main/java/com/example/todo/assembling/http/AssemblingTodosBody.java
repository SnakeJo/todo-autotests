package com.example.todo.assembling.http;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.todo.domain.http.TodoBodyDomain;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssemblingTodosBody {

    public <T, K, V> Map<String, Object> getAnyTypeValueBody(T id, K text, V completed) {
        Map<String, Object> body = new HashMap<>();
        if (id != null) {
            body.put("id", id);
        }
        if (text != null) {
            body.put("text", text);
        }
        if (completed != null) {
            body.put("completed", completed);
        }
        return body;
    }

    public TodoBodyDomain getBody(Long id, String text, Boolean completed) {
        return TodoBodyDomain.builder()
                .id(id)
                .text(text)
                .completed(completed)
                .build();
    }
}
