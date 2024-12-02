package com.example.todo.http.delete;

import static com.example.todo.utils.DataHelper.generateRandomBoolean;
import static com.example.todo.utils.DataHelper.generateRandomLong;
import static com.example.todo.utils.DataHelper.generateTodoText;
import static com.example.todo.utils.DataHelper.parseResponseOfTodoBodyDomains;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.beans.factory.annotation.Autowired;

import com.example.todo.TestsConfiguration;
import com.example.todo.assembling.http.AssemblingTodosBody;

@Tags({
    @Tag("http"),
    @Tag("delete")})
public class DeleteTodosTests extends TestsConfiguration{
 
    @Autowired
    protected AssemblingTodosBody assemblingTodosBody;

    private static final String NAME_AUTH = "admin";
    private static final String PASSWORD_AUTH = "admin";

    @BeforeAll
    void restartTodoAppContainer() {
        todoAppHelper.restartTodoApp();
    }

    static Stream<Arguments> invalidSetWithWrongNameOrPassword() {
        return Stream.of(
            Arguments.of(generateTodoText(),  PASSWORD_AUTH),
            Arguments.of(NAME_AUTH, generateTodoText())
        );
    }

    @Test
    @DisplayName("Valid request delete todo")
    void checkValidDeleteTodo() {
        
        var body = step("create todo body", () -> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), generateRandomBoolean())
        ); 

        step("send POST /todos", () -> apiHttpManager.createTodos(body));

        var response = step("send DELETE /todos/id and get response", () ->
            apiHttpManager.deleteTodos(NAME_AUTH, PASSWORD_AUTH, body.getId().toString())
        );

        var listTodo = step("send GET /todos and get response", () -> 
            parseResponseOfTodoBodyDomains(apiHttpManager.getTodos(Map.of()))
        );

        System.out.println(response.jsonPath().toString());
        step("check response", () -> {
            assertEquals(204, response.statusCode());
            assertFalse(listTodo.contains(body));
        });
    }

    @ParameterizedTest
    @DisplayName("Valid request delete todo")
    @MethodSource("invalidSetWithWrongNameOrPassword")
    void checkInvalidRequestWithWrongNameOrPassword(String nameAuth, String passwordAuth) {

        var body = step("create todo body", () -> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), generateRandomBoolean())
        ); 

        step("send POST /todos", () -> apiHttpManager.createTodos(body));

        var response = step("send DELETE /todos/id and get response", () ->
            apiHttpManager.deleteTodos(nameAuth, passwordAuth, body.getId().toString())
        );

        var listTodo = step("send GET /todos and get response", () -> 
            parseResponseOfTodoBodyDomains(apiHttpManager.getTodos(Map.of()))
        );

        System.out.println(response.jsonPath().toString());
        step("check response", () -> {
            assertEquals(401, response.statusCode());
            assertTrue(listTodo.contains(body));
        });
    }

    @Test
    @DisplayName("Error deleting todo on non-existent ID fields")
    void checkInvalidRequestWithNonExistentId() {
        
        var response = step("send DELETE /todos/id and get response", () ->
            apiHttpManager.deleteTodos(NAME_AUTH, PASSWORD_AUTH, String.valueOf(generateRandomLong()))
        );

        step("check response", () -> {
            assertEquals(404, response.statusCode());
        });
    }
}
