package com.example.todo.http.post;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.example.todo.utils.DataHelper.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.todo.TestsConfiguration;

@Tags({
    @Tag("http"),
    @Tag("post")})
public class CreateTodosTests extends TestsConfiguration{

    @BeforeAll
    void restartTodoAppContainer() {
        todoAppHelper.restartTodoApp();
    }

    static Stream<Arguments> invalidSetWithoutRequiredFields() {
        return Stream.of(
            Arguments.of(null, generateTodoText(), true, "id"),
            Arguments.of(generateRandomLong(), null, false, "text"),
            Arguments.of(generateRandomLong(), generateTodoText(), null, "completed")
        );
    }

    @ParameterizedTest
    @DisplayName("Create valid todo")
    @ValueSource(booleans = {true, false})
    void checkValidCreateTodo(boolean isCompleted) {
       
        var body = step("create todo body", ()-> 
            assemblingTodosBody.getBody(generateRandomLong(), generateTodoText(), isCompleted)
        ); 
    
        var response = step("send POST /todos and get response", () -> apiHttpManager.createTodos(body));

        step("check response", () -> {
           assertEquals(201, response.statusCode());
           assertEquals("", response.body().asString());
        });
    }

    @ParameterizedTest
    @DisplayName("Error creating todo without required fields")
    @MethodSource("invalidSetWithoutRequiredFields")
    void checkInvalidRequestWithoutRequiredFields(Long id, String title, Boolean isCompleted, String nameEmptyField) {
       
        var body = step("create todo body", ()-> 
            assemblingTodosBody.getAnyTypeValueBody(id, title, isCompleted)
        ); 
   
        var response = step("send POST /todos and get response", () -> apiHttpManager.createTodos(body));

        var expectedMessage = step("Prepare expected message", () -> 
            String.format("Request body deserialize error: missing field `%s`", nameEmptyField)
        );

        step("check response", () -> {
           assertEquals(400, response.statusCode());
           assertTrue(response.body().asString().contains(expectedMessage), 
           "Response body does not contain expected message");
        });
    }

    @Test
    @DisplayName("Error creating todo with wrong type ID fields")
    void checkInvalidRequestWithWrongTypeIdFields() {
       
        var body = step("create todo body", () -> 
            assemblingTodosBody.getAnyTypeValueBody(generateRandomTypeValue(), generateTodoText(), generateRandomBoolean())
        ); 
   
        var response = step("send POST /todos and get response", () -> apiHttpManager.createTodos(body));

        step("check response", () -> {
           assertEquals(400, response.statusCode());
           assertTrue(response.body().asString().contains("Request body deserialize error: invalid type:"),
           "Response body does not contain expected message");
        });
    }

    @Test
    @DisplayName("Error creating todo with wrong type TEXT fields")
    void checkInvalidRequestWithWrongTypeTextFields() {
       
        var body = step("create todo body", () -> 
            assemblingTodosBody.getAnyTypeValueBody(generateRandomLong(),
                                                generateRandomTypeValueWithExclude(0),
                                                generateRandomBoolean())
        ); 
   
        var response = step("send POST /todos and get response", () -> apiHttpManager.createTodos(body));

        step("check response", () -> {
           assertEquals(400, response.statusCode());
           assertTrue(response.body().asString().contains("Request body deserialize error: invalid type:"),
           "Response body does not contain expected message");
        });
    }

    @Test
    @DisplayName("Error creating todo with wrong type COMPLETED fields")
    void checkInvalidRequestWithWrongTypeCompletedFields() {
       
        var body = step("create todo body", () -> 
            assemblingTodosBody.getAnyTypeValueBody(generateRandomLong(),
                                                generateTodoText(),
                                                generateRandomTypeValueWithExclude(2))
        ); 
   
        var response = step("send POST /todos and get response", () -> apiHttpManager.createTodos(body));

        step("check response", () -> {
           assertEquals(400, response.statusCode());
           assertTrue(response.body().asString().contains("Request body deserialize error: invalid type:"),
           "Response body does not contain expected message");
        });
    }
    
    @Test
    @DisplayName("Error creating todo with existing ID fields")
    void checkInvalidRequestWithExistingId() {

        var id = generateRandomLong();

        var firstBody = step("create todo first body", () -> 
            assemblingTodosBody.getBody(id, generateTodoText(), generateRandomBoolean())
        ); 

        step("send POST /todos and get response", () -> apiHttpManager.createTodos(firstBody));

        var secondBody = step("create todo second body", () -> 
            assemblingTodosBody.getBody(id, generateTodoText(), generateRandomBoolean())
        ); 

        var secondResponse = step("send POST /todos and get response", () ->
            apiHttpManager.createTodos(secondBody)
        );

        step("check response", () -> {
           assertEquals(400, secondResponse.statusCode(),
            "Error creating todo with existing ID fields");
        // TODO: add a understandable message for existing ID error
        });
    }

    @Test
    @DisplayName("Error creating todo with greater than possible ID fields")
    void checkInvalidRequestWithGreaterThanPosibleIdFields() {
       
        var body = step("create todo body", () -> 
            assemblingTodosBody.getAnyTypeValueBody(BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE),
                                                generateTodoText(),
                                                generateRandomBoolean())
        ); 
   
        var response = step("send POST /todos and get response", () -> apiHttpManager.createTodos(body));

        step("check response", () -> {
           assertEquals(400, response.statusCode(), "Error creating todo with greater than possible ID fields");
           // TODO: there should be an error here because the specified id is greater than possible
           // now: Creating a todo with an id that is greater than possible
        });
    }
}
