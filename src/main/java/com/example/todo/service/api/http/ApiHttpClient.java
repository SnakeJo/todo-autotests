package com.example.todo.service.api.http;

import lombok.RequiredArgsConstructor;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static java.util.Base64.getEncoder;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiHttpClient {

    public Response getRequest(String baseUri, String pathResourse, Map<String, Object> queryParams) {
        return given()
                .log().all()
                .baseUri(baseUri)
                .queryParams(queryParams)
                .get(pathResourse);
    }
   
    public Response postRequest(String baseUri, String pathResourse, Object body) {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .baseUri(baseUri)
                .body(body)
                .post(pathResourse);
    }

    public Response putRequest(String baseUri, String pathResourse, String pathParam, Object body) {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .baseUri(baseUri)
                .body(body)
                .put(String.format("%s/%s", pathResourse, pathParam));
    }

    public Response deleteRequest(String nameAuth, String passwordAuth, String baseUri,
                                                 String pathResourse, String pathParam) {
        return given()
                .log().all()
                .header("Authorization",
                        "Basic " + getEncoder()
                        .encodeToString(String.format("%s:%s", nameAuth, passwordAuth).getBytes()))
                .baseUri(baseUri)
                .delete(String.format("%s/%s", pathResourse, pathParam));
    }
}
