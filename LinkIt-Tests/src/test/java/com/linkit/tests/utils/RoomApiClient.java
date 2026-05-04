package com.linkit.tests.utils;

import com.linkit.tests.models.RoomResponse;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class RoomApiClient {
    private final RequestSpecification spec;

    public RoomApiClient(RequestSpecification spec) {
        this.spec = spec;
    }

    public RoomResponse createRoom(String name) {
        Map<String, Object> request = new HashMap<>();
        request.put("name", name);

        return given()
                .spec(spec)
                .body(request)
                .when()
                .post("/createRoom")
                .then()
                .statusCode(200)
                .extract()
                .as(RoomResponse.class);
    }

    public RoomResponse joinRoom(String roomId, String name) {
        Map<String, Object> request = new HashMap<>();
        request.put("roomId", roomId);
        request.put("name", name);

        return given()
                .spec(spec)
                .body(request)
                .when()
                .post("/joinRoom")
                .then()
                .statusCode(200)
                .extract()
                .as(RoomResponse.class);
    }

    public void removeParticipant(String roomId, String name) {
        given()
                .spec(spec)
                .queryParam("roomId", roomId)
                .queryParam("participantName", name)
                .when()
                .delete("/removeParticipant");
    }
}

