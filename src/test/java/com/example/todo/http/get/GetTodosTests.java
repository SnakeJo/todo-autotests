package com.example.todo.http.get;

import static com.example.todo.utils.DataHelper.generateRandomBoolean;
import static com.example.todo.utils.DataHelper.generateRandomLong;
import static com.example.todo.utils.DataHelper.generateTodoText;
import static com.example.todo.utils.DataHelper.parseResponseOfTodoBodyDomains;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.todo.TestsConfiguration;
import com.example.todo.assembling.http.AssemblingGetTodosQueryParams;
import com.example.todo.assembling.http.AssemblingTodosBody;

@Tags({
    @Tag("http"),
    @Tag("get")})
public class GetTodosTests extends TestsConfiguration{

    @Autowired
    protected AssemblingTodosBody assemblingTodosBody;

    @Autowired
    protected AssemblingGetTodosQueryParams assemblingGetTodosQueryParams;

    @BeforeAll
    void restartTodoAppContainer() {
        todoAppHelper.restartTodoApp();
    }

    @Test
    @DisplayName("Get todos without query params")
    void checkValidRequestWithoutQueryParams() {

        var body = step("create todo body", ()-> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), generateRandomBoolean())
        ); 
    
        step("send POST /todos and get response", () -> apiHttpManager.createTodos(body));

        var queryParams = step("create query params", () -> 
            assemblingGetTodosQueryParams.getParams(null, null)
        );

        var response = step("send GET /todos", () -> apiHttpManager.getTodos(queryParams));

        step("check response", () -> {
            assertEquals(200, response.statusCode());
            assertTrue(parseResponseOfTodoBodyDomains(response).contains(body));
        });
    }

    @ParameterizedTest
    @DisplayName("Get todos with query params: OFFSET")
    @ValueSource(ints = {0, 1, 2000})
    void checkValidRequestWithOffsetQueryParams(int offset) {

        var initialListTodos = step("send GET /todos and get response", () -> {
            var params = assemblingGetTodosQueryParams.getParams(null, null);
            return parseResponseOfTodoBodyDomains(apiHttpManager.getTodos(params));
        });

        var lastTodo = step("create todo body", ()-> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), generateRandomBoolean())
        );

        step("send POST /todos", () -> apiHttpManager.createTodos(lastTodo));

        var currentListTodos = step("send GET /todos and get response", () -> {
            var params = assemblingGetTodosQueryParams.getParams(offset, null);
            return parseResponseOfTodoBodyDomains(apiHttpManager.getTodos(params));
        });

        if (offset == 0) {
            step("check result", () -> {
                assertTrue(currentListTodos.containsAll(initialListTodos));
            });
        }
        if (offset == 1) {
            step("check result", () -> {
                assertTrue(currentListTodos.size() == initialListTodos.size());
                assertTrue(currentListTodos.contains(lastTodo));
            });
        }
        if (offset == 2000) {
            step("check result", () -> {
                assertTrue(currentListTodos.isEmpty());
            });
        }
    }

    @ParameterizedTest
    @DisplayName("Get todos with query params: LIMIT")
    @ValueSource(ints = {1, 0, 2000})
    void checkValidRequestWithLimitQueryParams(int limit) {

        step("create todo body and send POST /todos", ()->{ 
            var lastTodo = assemblingTodosBody.getBody(generateRandomLong(),
                                                generateTodoText(), generateRandomBoolean());
            apiHttpManager.createTodos(lastTodo);
        });

        var initialListTodos = step("send GET /todos and get response", () -> {
            var params = assemblingGetTodosQueryParams.getParams(null, null);
            return parseResponseOfTodoBodyDomains(apiHttpManager.getTodos(params));
        });

        step("create todo body and send POST /todos", ()->{ 
            var lastTodo = assemblingTodosBody.getBody(generateRandomLong(),
                                                generateTodoText(), generateRandomBoolean());
            apiHttpManager.createTodos(lastTodo);
        });

        var currentListTodos = step("send GET /todos and get response", () -> {
            var params = assemblingGetTodosQueryParams.getParams(null, limit);
            return parseResponseOfTodoBodyDomains(apiHttpManager.getTodos(params));
        });

        if (limit == 1) {
            step("check result", () -> {
                assertTrue(currentListTodos.size() == limit);
                assertTrue(currentListTodos.get(0).equals(initialListTodos.get(0)));
            });
        }
        if (limit == 0) {
            step("check result", () -> {
                assertTrue(currentListTodos.isEmpty());
            });
        }
        if (limit == 2000) {
            step("check result", () -> {
                assertTrue(currentListTodos.size() == initialListTodos.size() + 1);
            });
        }
    }

    // TODO: add test:
    // - Get todos with query params: OFFSET and LIMIT
    // - Error request with wrong OFFSET
    // - Error request with wrong LIMIT

}
