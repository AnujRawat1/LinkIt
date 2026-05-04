package com.linkit.tests.config;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestConfigTests {

    @Test
    public void defaultsShouldBeUsableForLocalExecution() {
        Assert.assertTrue(TestConfig.API_BASE_URL.startsWith("http"), "API base URL should be http(s)");
        Assert.assertTrue(TestConfig.UI_BASE_URL.startsWith("http"), "UI base URL should be http(s)");
        Assert.assertTrue(TestConfig.DEFAULT_TIMEOUT_SECONDS > 0, "Timeout should be positive");
    }
}

