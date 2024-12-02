package com.example.todo.utils;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.todo.domain.http.TodoBodyDomain;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataHelper {

    public static String generateTodoText() {
        return generateRandomString(10);
    }
    
    public static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            randomString.append(chars.charAt(randomIndex));
        }
        return randomString.toString();
    }

    public static long generateRandomLong() {
        return (long) (Math.random() * Long.MAX_VALUE);
    }

    public static boolean generateRandomBoolean() {
        return Math.random() < 0.5;
    }

    public static Object generateRandomTypeValue() {
        int randomType = (int) (Math.random() * 3);
        switch (randomType) {
            case 0:
                return generateRandomString(10);
            case 1:
                return Math.random();
            case 2:
                return generateRandomBoolean();
            default:
                return null;
        }
    }

    /**
     * Generates a random value of a randomly selected type, excluding the specified type.
     * The possible types are:
     * 0 - String,
     * 1 - Double,
     * 2 - Boolean.
     *
     * @param excludeType the type to exclude from selection
     * @return a random value of the selected type, not including the excluded type
     */
    public static Object generateRandomTypeValueWithExclude(int excludeType) {
        int randomType;
        do {
            randomType = (int) (Math.random() * 3);
        } while (randomType == excludeType);

        switch (randomType) {
            case 0:
                return generateRandomString(10);
            case 1:
                return Math.random();
            case 2:
                return generateRandomBoolean();
            default:
                return null;
        }
    }

    // public static TodoBodyDomain parseTodoBodyDomains(Map<String, Object> data) {
    //     try {
    //         Long id = (Long) data.get("id");
    //         String text = (String) data.get("text");
    //         Boolean completed = (Boolean) data.get("completed");
    //         return new TodoBodyDomain(id, text, completed);
    //     } catch (ClassCastException | NullPointerException e) {
    //         // Handle parsing error
    //         System.err.println("Error parsing TodoBodyDomain: " + e.getMessage());
    //     }
    //     return null;
    // }

    public static List<TodoBodyDomain> parseResponseOfTodoBodyDomains(Response response) {
        try {
            return response.jsonPath().getList(".", TodoBodyDomain.class);
        } catch (Exception e) {
            // Handle parsing error
            System.err.println("Error parsing TodoBodyDomain: " + e.getMessage());
        }
        return null;
    }

    public static TodoBodyDomain findTodoById(Long id, List<TodoBodyDomain> todoBody) {
        for (TodoBodyDomain todo : todoBody) {
            if (todo.getId().equals(id)) {
                return todo;
            }
        }
        return null;
    }
    
}
