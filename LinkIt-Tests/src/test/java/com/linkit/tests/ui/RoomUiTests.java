package com.linkit.tests.ui;

import com.linkit.tests.base.BaseUiTest;
import com.linkit.tests.config.TestConfig;
import com.linkit.tests.models.RoomResponse;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;

public class RoomUiTests extends BaseUiTest {
    private final Map<String, Set<String>> roomsForCleanup = new HashMap<>();

    @AfterMethod(alwaysRun = true)
    public void cleanupRoomsAndBrowserState() {
        for (Map.Entry<String, Set<String>> room : roomsForCleanup.entrySet()) {
            for (String participant : room.getValue()) {
                try {
                    given()
                            .baseUri(TestConfig.API_BASE_URL)
                            .basePath("/api/room")
                            .queryParam("roomId", room.getKey())
                            .queryParam("participantName", participant)
                            .delete("/removeParticipant");
                } catch (Exception ignored) {
                    // Ignore cleanup errors to keep test result focused on primary assertions.
                }
            }
        }
        roomsForCleanup.clear();

        if (driver != null) {
            driver.manage().deleteAllCookies();
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        }
    }

    @Test(description = "Homepage should render both create and join tabs")
    public void homePageShouldRenderPrimaryControls() {
        openHome();

        WebElement createTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='create-tab-btn']")));
        WebElement joinTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='join-tab-btn']")));
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='name-input']")));

        Assert.assertTrue(createTab.isDisplayed(), "Create tab should be visible");
        Assert.assertTrue(joinTab.isDisplayed(), "Join tab should be visible");
        Assert.assertTrue(nameInput.isDisplayed(), "Name input should be visible");
    }

    @Test(description = "Join tab should show room id input")
    public void joinTabShouldRevealRoomIdField() {
        openHome();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='join-tab-btn']"))).click();

        WebElement roomIdInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='room-id-input']")));
        Assert.assertTrue(roomIdInput.isDisplayed(), "Room ID input should be visible after switching to join tab");
    }

    @Test(description = "Create flow should navigate to room page and render room id badge")
    public void createRoomFlowShouldOpenRoomPage() {
        String username = unique("ui-owner");

        openHome();
        type(By.cssSelector("[data-testid='name-input']"), username);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='create-room-btn']"))).click();

        wait.until(ExpectedConditions.urlContains("/room/"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/room/"), "URL should navigate to room page");

        WebElement badge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='room-id-badge']")));
        Assert.assertFalse(badge.getText().trim().isEmpty(), "Room ID badge should have value");

        String createdRoomId = extractRoomIdFromUrl(currentUrl);
        trackParticipant(createdRoomId, username);

        WebElement participants = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='participants-list']")));
        Assert.assertTrue(participants.getText().contains(username), "Participants panel should include room creator");
    }

    @Test(description = "Joining existing room should navigate to the same room id")
    public void joinExistingRoomShouldNavigateToRoom() {
        String owner = unique("api-owner");
        String guest = unique("ui-guest");
        RoomResponse room = createRoomViaApi(owner);
        trackParticipant(room.roomId, owner);

        openHome();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='join-tab-btn']"))).click();
        type(By.cssSelector("[data-testid='name-input']"), guest);
        type(By.cssSelector("[data-testid='room-id-input']"), room.roomId);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='join-room-btn']"))).click();

        wait.until(ExpectedConditions.urlContains("/room/" + room.roomId));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/room/" + room.roomId), "Should navigate to selected room");

        WebElement participants = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='participants-list']")));
        Assert.assertTrue(participants.getText().contains(guest), "Participants panel should include joined user");
        trackParticipant(room.roomId, guest);
    }

    @Test(description = "Invalid join should stay on home and show toast error")
    public void invalidJoinShouldShowErrorToast() {
        openHome();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='join-tab-btn']"))).click();

        type(By.cssSelector("[data-testid='name-input']"), unique("ghost"));
        type(By.cssSelector("[data-testid='room-id-input']"), "ZZZZZZ");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='join-room-btn']"))).click();

        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(@class,'Toastify__toast') and contains(., 'Room Does not exist')]")));

        Assert.assertTrue(toast.isDisplayed(), "Error toast should be visible for invalid room");
        Assert.assertFalse(driver.getCurrentUrl().contains("/room/"), "Should stay on home route when join fails");
    }

    @Test(description = "Deep-linking to room route should render room with URL room id")
    public void deepLinkRoomRouteShouldLoadWithUrlParam() {
        String owner = unique("deep-owner");
        RoomResponse room = createRoomViaApi(owner);
        trackParticipant(room.roomId, owner);

        driver.get(TestConfig.UI_BASE_URL + "/room/" + room.roomId);

        WebElement badge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='room-id-badge']")));
        Assert.assertEquals(badge.getText().trim(), room.roomId, "Room page should use URL param for room id");
    }

    private void openHome() {
        driver.get(TestConfig.UI_BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='name-input']")));
    }

    private void type(By locator, String value) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        input.clear();
        input.sendKeys(value);
    }

    private String unique(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private RoomResponse createRoomViaApi(String owner) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", owner);

        return given()
                .baseUri(TestConfig.API_BASE_URL)
                .basePath("/api/room")
                .contentType("application/json")
                .body(payload)
                .when()
                .post("/createRoom")
                .then()
                .statusCode(200)
                .extract()
                .as(RoomResponse.class);
    }

    private String extractRoomIdFromUrl(String url) {
        int idx = url.lastIndexOf("/room/");
        if (idx < 0) {
            throw new AssertionError("Room URL format invalid: " + url);
        }
        return url.substring(idx + 6);
    }

    private void trackParticipant(String roomId, String participant) {
        roomsForCleanup.computeIfAbsent(roomId, id -> new HashSet<>()).add(participant);
    }
}

