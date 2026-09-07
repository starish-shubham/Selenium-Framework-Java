package org.example.core.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.example.core.DriverFactory;
import org.example.core.reports.ExtentManager;
import org.example.core.reports.ExtentTestManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Map;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        // Runs once before any test methods execute
        Map<String, String> xmlParams = context.getCurrentXmlTest().getAllParameters();

        String environment = xmlParams.getOrDefault("environment", "QA");
        String browser = xmlParams.getOrDefault("browser", "Chrome");

        // Set metadata on Extent dashboard
        ExtentManager.setSystemInfo("Environment", environment);
        ExtentManager.setSystemInfo("Browser", browser);
        ExtentManager.setSystemInfo("Suite Name", context.getSuite().getName());
        ExtentManager.setSystemInfo("OS", System.getProperty("os.name"));
        ExtentManager.setSystemInfo("User", System.getProperty("user.name"));
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.startTest(
                result.getMethod().getMethodName(),
                result.getMethod().getDescription()
        );
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.getTest().log(Status.PASS, "Test Passed");
        ExtentTestManager.unload();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTestManager.getTest().log(Status.FAIL, result.getThrowable());

        try {
            String base64Screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BASE64);

            ExtentTestManager.getTest().fail(
                    "Failure Screenshot",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build()
            );
        } catch (Exception e) {
            ExtentTestManager.getTest().log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
        } finally {
            ExtentTestManager.unload();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTestManager.getTest().log(Status.SKIP, "Test Skipped: " + result.getThrowable());
        ExtentTestManager.unload();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getInstance().flush();
    }
}