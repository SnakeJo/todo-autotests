package com.example.todo.http.put;

import static com.example.todo.utils.DataHelper.generateRandomBoolean;
import static com.example.todo.utils.DataHelper.generateRandomLong;
import static com.example.todo.utils.DataHelper.generateTodoText;
import static com.example.todo.utils.DataHelper.parseResponseOfTodoBodyDomains;
import static com.example.todo.utils.DataHelper.findTodoById;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.todo.TestsConfiguration;

@Tags({
    @Tag("http"),
    @Tag("put")})
public class UpdateTodosTests extends TestsConfiguration{

    @BeforeAll
    void restartTodoAppContainer() {
        todoAppHelper.restartTodoApp();
    }

    static Stream<Arguments> setUpdateCompleted() {
        return Stream.of(
            Arguments.of(true,  false),
            Arguments.of(false, true)
        );
    }

    @ParameterizedTest
    @DisplayName("Update valid todo")
    @MethodSource("setUpdateCompleted")
    void checkValidUpdateTodo(boolean initialComplted, boolean updatedCompleted) {
        
        var initialBody = step("create todo initial body", ()-> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), initialComplted)
        );  

        step("send POST /todos", () -> apiHttpManager.createTodos(initialBody));

        var updatedBody = step("create todo updated body", ()-> 
            assemblingTodosBody.getBody(initialBody.getId(), generateTodoText(), updatedCompleted)
        );

        var response = step("send PUT /todos/id and get response", () ->
            apiHttpManager.updateTodos(initialBody.getId().toString(), updatedBody));

        var expectedBody = step("get todo expected body", () -> {
            var body = parseResponseOfTodoBodyDomains(apiHttpManager.getTodos(Map.of()));
            return findTodoById(initialBody.getId(), body);
        });

        step("check response", () -> {
            assertEquals(200, response.statusCode());
            assertEquals("", response.body().asString());
            assertEquals(expectedBody, updatedBody, "The todo was not updated");
        });
    }

    @Test
    @DisplayName("Error updating todo on non-existent ID fields")
    void checkInvalidRequestUpdateBodyOnNonExistentId() {
        
        var body = step("create todo body", () -> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), generateRandomBoolean())
        ); 

        step("send POST /todos", () -> apiHttpManager.createTodos(body));

        var response = step("send PUT /todos/id and get response", () ->
            apiHttpManager.updateTodos(String.valueOf(generateRandomLong()), body)
        );

        step("check response", () -> {
            assertEquals(404, response.statusCode(), "The todo was updated");
        });
    }

    @Test
    @DisplayName("Error updating todo on existent ID fields")
    void checkInvalidRequestUpdateBodyOnExistentId() {

        var firstBody = step("create first todo body", () -> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), generateRandomBoolean())
        );
        
        var secondBody = step("create second todo body", () -> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), generateRandomBoolean())
        );

        step("send POST /todos", () -> {
            apiHttpManager.createTodos(firstBody);
            apiHttpManager.createTodos(secondBody);
        });

        var response = step("send PUT /todos/id and get response", () ->
            apiHttpManager.updateTodos(firstBody.getId().toString(), secondBody)
        );

        step("check response", () -> {
            assertEquals(401, response.statusCode(), "The todo was updated");
            // TODO: an error and a message about mismatched ID or invalid request should be returned here
            // currently, the ID of the todo is being changed. The system ends up with two entities having the same ID.
        });
    }

    // TODO: add test:
    // - for updating todo with greater than possible ID
    // - for updating todo with wrong type ID
    // - for updating todo with wrong type TEXT
    // - for updating todo with wrong type COMPLETED

}
