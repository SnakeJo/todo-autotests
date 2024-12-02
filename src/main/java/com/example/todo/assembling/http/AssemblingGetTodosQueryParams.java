package com.example.todo.assembling.http;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssemblingGetTodosQueryParams {

    public Map<String, Object> getParams(Integer offset, Integer limit) {
        Map<String, Object> queryParams = new HashMap<>();
        if (offset != null) {
            queryParams.put("offset", offset);
        }
        if (limit != null) {
            queryParams.put("limit", limit);
        }
        return queryParams;
    }
}
