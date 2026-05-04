package com.linkit.tests.api;

import com.linkit.tests.base.BaseApiTest;
import com.linkit.tests.models.RoomResponse;
import com.linkit.tests.utils.RoomApiClient;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;

public class RoomApiTests extends BaseApiTest {
    private static final String DEFAULT_CONTENT = "// Start typing your Ideas here... \n\n";

    private RoomApiClient roomApiClient;
    private final Map<String, Set<String>> createdRooms = new HashMap<>();

    @BeforeMethod(alwaysRun = true)
    public void initClient() {
        roomApiClient = new RoomApiClient(apiSpec);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupRooms() {
        for (Map.Entry<String, Set<String>> entry : createdRooms.entrySet()) {
            String roomId = entry.getKey();
            for (String participant : entry.getValue()) {
                roomApiClient.removeParticipant(roomId, participant);
            }
        }
        createdRooms.clear();
    }

    @Test(description = "Create room should return room details and creator as first participant")
    public void createRoomShouldReturnDefaults() {
        String owner = uniqueName("owner");

        RoomResponse room = roomApiClient.createRoom(owner);
        trackParticipant(room.roomId, owner);

        Assert.assertNotNull(room.roomId, "roomId should not be null");
        Assert.assertEquals(room.roomId.length(), 6, "roomId should be 6 chars");
        Assert.assertEquals(room.content, DEFAULT_CONTENT, "Default content should match template");
        Assert.assertNotNull(room.participants, "participants list should not be null");
        Assert.assertTrue(room.participants.contains(owner), "creator should be present in participants");
        Assert.assertNotNull(room.fileNames, "fileNames list should be initialized");
    }

    @Test(description = "Two room creation requests should generate unique room ids")
    public void roomIdsShouldBeUniqueAcrossCreates() {
        RoomResponse first = roomApiClient.createRoom(uniqueName("a"));
        RoomResponse second = roomApiClient.createRoom(uniqueName("b"));

        trackParticipant(first.roomId, first.participants.get(0));
        trackParticipant(second.roomId, second.participants.get(0));

        Assert.assertNotEquals(first.roomId, second.roomId, "Room IDs should be unique");
    }

    @Test(description = "Join room should append second participant")
    public void joinRoomShouldAddParticipant() {
        String owner = uniqueName("owner");
        String guest = uniqueName("guest");

        RoomResponse created = roomApiClient.createRoom(owner);
        trackParticipant(created.roomId, owner);

        RoomResponse joined = roomApiClient.joinRoom(created.roomId, guest);
        trackParticipant(created.roomId, guest);

        Assert.assertTrue(joined.participants.contains(owner), "owner should remain in room");
        Assert.assertTrue(joined.participants.contains(guest), "guest should be added to room");
    }

    @Test(description = "Join invalid room should return 204")
    public void joinInvalidRoomShouldReturnNoContent() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("roomId", "ZZZZZZ");
        payload.put("name", uniqueName("ghost"));

        int statusCode = given()
                .spec(apiSpec)
                .body(payload)
                .when()
                .post("/joinRoom")
                .then()
                .extract()
                .statusCode();

        Assert.assertEquals(statusCode, 204, "Invalid room join should return 204");
    }

    @Test(description = "Get content should return default editor text for a new room")
    public void getContentShouldReturnDefaultContent() {
        String owner = uniqueName("owner");
        RoomResponse created = roomApiClient.createRoom(owner);
        trackParticipant(created.roomId, owner);

        String content = given()
                .spec(apiSpec)
                .queryParam("roomId", created.roomId)
                .when()
                .get("/getContent")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        Assert.assertEquals(content, DEFAULT_CONTENT, "Content should match backend default");
    }

    @Test(description = "Get participants should return both owner and guest after join")
    public void getParticipantsShouldReflectJoins() {
        String owner = uniqueName("owner");
        String guest = uniqueName("guest");
        RoomResponse created = roomApiClient.createRoom(owner);
        trackParticipant(created.roomId, owner);
        roomApiClient.joinRoom(created.roomId, guest);
        trackParticipant(created.roomId, guest);

        List<String> participants = given()
                .spec(apiSpec)
                .queryParam("roomId", created.roomId)
                .when()
                .get("/getParticipants")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("$", String.class);

        Assert.assertTrue(participants.contains(owner), "Owner should be listed");
        Assert.assertTrue(participants.contains(guest), "Guest should be listed");
        Assert.assertTrue(participants.size() >= 2, "Participants should include at least owner and guest");
    }

    @Test(description = "Get file names should return empty list for a new room")
    public void getFileNamesShouldStartEmpty() {
        String owner = uniqueName("owner");
        RoomResponse created = roomApiClient.createRoom(owner);
        trackParticipant(created.roomId, owner);

        List<String> fileNames = given()
                .spec(apiSpec)
                .queryParam("roomId", created.roomId)
                .when()
                .get("/getFileNames")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("$", String.class);

        Assert.assertTrue(fileNames.isEmpty(), "New room should not have file names");
    }

    @Test(description = "Removing only participant should delete the room")
    public void removeLastParticipantShouldDeleteRoom() {
        String owner = uniqueName("solo");
        RoomResponse created = roomApiClient.createRoom(owner);

        int removeStatus = given()
                .spec(apiSpec)
                .queryParam("roomId", created.roomId)
                .queryParam("participantName", owner)
                .when()
                .delete("/removeParticipant")
                .then()
                .extract()
                .statusCode();

        Assert.assertEquals(removeStatus, 200, "Remove participant should return 200");

        int contentStatus = given()
                .spec(apiSpec)
                .queryParam("roomId", created.roomId)
                .when()
                .get("/getContent")
                .then()
                .extract()
                .statusCode();

        Assert.assertEquals(contentStatus, 204, "Room should no longer exist after last participant leaves");
    }

    @Test(description = "Remove participant from unknown room should return 204")
    public void removeParticipantFromUnknownRoomShouldReturnNoContent() {
        int status = given()
                .spec(apiSpec)
                .queryParam("roomId", "UNKNOWN")
                .queryParam("participantName", "no-user")
                .when()
                .delete("/removeParticipant")
                .then()
                .extract()
                .statusCode();

        Assert.assertEquals(status, 204, "Unknown room remove operation should return 204");
    }

    @Test(description = "Get participants for unknown room should return 204")
    public void getParticipantsForUnknownRoomShouldReturnNoContent() {
        int status = given()
                .spec(apiSpec)
                .queryParam("roomId", "UNKNOWN")
                .when()
                .get("/getParticipants")
                .then()
                .extract()
                .statusCode();

        Assert.assertEquals(status, 204, "Unknown room participant query should return 204");
    }

    private String uniqueName(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void trackParticipant(String roomId, String participant) {
        createdRooms.computeIfAbsent(roomId, id -> new HashSet<>()).add(participant);
    }
}

