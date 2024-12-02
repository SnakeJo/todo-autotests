package com.example.todo.config.todoapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class TodoAppCofiguration {
    
    @Value("${todoapp.image}")
    private String imageName;
}
