package com.linkit.tests.base;

import com.linkit.tests.config.TestConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

public abstract class BaseUiTest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeClass(alwaysRun = true)
    public void setupUi() {
        ensureUiIsReachableOrSkip();
        driver = createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT_SECONDS));
    }

    @AfterClass(alwaysRun = true)
    public void tearDownUi() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebDriver createDriver() {
        String browser = TestConfig.BROWSER.toLowerCase();
        switch (browser) {
            case "edge": {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (TestConfig.HEADLESS) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1920,1080");
                return new EdgeDriver(options);
            }
            case "chrome":
            default: {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (TestConfig.HEADLESS) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--window-size=1920,1080");
                options.addArguments("--remote-allow-origins=*");
                return new ChromeDriver(options);
            }
        }
    }

    private void ensureUiIsReachableOrSkip() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(TestConfig.UI_BASE_URL).openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            connection.connect();
            connection.getResponseCode();
        } catch (Exception ex) {
            throw new SkipException("Frontend not reachable at " + TestConfig.UI_BASE_URL + ". Start LinkIt-Frontend and rerun tests.", ex);
        }
    }
}

