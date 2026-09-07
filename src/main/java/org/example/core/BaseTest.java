package org.example.core;

import org.example.core.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.util.Map;

public class BaseTest {

    public Map<String, String> params;
    public static String baseURI;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String browserParam = params.get("browser");
        String headlessParam = params.get("headless");
        // Fall back to config reader if XML parameters are not provided
        String browser = (browserParam != null && !browserParam.isEmpty())
                ? browserParam
                : ConfigReader.get("browser", "chrome");

        boolean headless = (headlessParam != null && !headlessParam.isEmpty())
                ? Boolean.parseBoolean(headlessParam)
                : Boolean.parseBoolean(ConfigReader.get("headless", "false"));

        // Initialize ThreadLocal driver
        DriverFactory.initDriver(browser, headless);

        // Maximize window if not headless (Chromium handles this via flags, but standard practice)
        if (!headless) {
            getDriver().manage().window().maximize();
            getDriver().manage().deleteAllCookies();
        }

        String baseUrl = initializeEnv();
        if (baseUrl != null && !baseUrl.isEmpty()) {
            getDriver().get(baseUrl);
        }
    }

    /**
     * Captures screenshot on failure and tears down the WebDriver session after each test method.
     *
     * @param result TestNG result object representing the executed test
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                // Takes screenshot using the failed test method's name
                ScreenshotUtils.captureScreenshot(getDriver(), result.getName());
            }
        } catch (Exception e) {
            System.err.println("Failed to capture failure screenshot: " + e.getMessage());
        } finally {
            DriverFactory.quitDriver();
        }
    }

    /**
     * Helper getter so test classes extending BaseTest can access the driver instance cleanly.
     */
    public WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    public String initializeEnv()
    {
        String environment = params.get("environment");
        String url = null;
        if (environment.equals("qa")) {
            url =  ConfigReader.get("qa.baseURI");
        } else if (environment.equals("staging")) {
            url =  ConfigReader.get("staging.baseURI");
        } else if (environment.equals("uat")) {
            url =  ConfigReader.get("uat.baseURI");
        } else if (environment.equals("prod")) {
            url = ConfigReader.get("prod.baseURI");
        }
        baseURI= url;
        return url;
    }
}