package com.example.todo;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.todo.service.api.http.ApiHttpManager;
import com.example.todo.utils.TodoAppHelper;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
@TestInstance(PER_CLASS)
@ExtendWith(SpringExtension.class)
public class TestsConfiguration {
    
    @Autowired
    protected ApiHttpManager apiHttpManager;

    @Autowired
    protected TodoAppHelper todoAppHelper;
}
