package com.linkit.tests.base;

import com.linkit.tests.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;

import java.net.HttpURLConnection;
import java.net.URL;

public abstract class BaseApiTest {
    protected RequestSpecification apiSpec;

    @BeforeClass(alwaysRun = true)
    public void setupApi() {
        ensureApiIsReachableOrSkip();

        RestAssured.baseURI = TestConfig.API_BASE_URL;
        apiSpec = new RequestSpecBuilder()
                .setBaseUri(TestConfig.API_BASE_URL)
                .setBasePath("/api/room")
                .setContentType("application/json")
                .build();
    }

    private void ensureApiIsReachableOrSkip() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(TestConfig.API_BASE_URL).openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            connection.connect();
            connection.getResponseCode();
        } catch (Exception ex) {
            throw new SkipException("Backend not reachable at " + TestConfig.API_BASE_URL + ". Start LinkitBackend and rerun tests.", ex);
        }
    }
}

