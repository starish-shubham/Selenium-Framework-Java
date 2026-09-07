package org.example.core.utils;

import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {

    private static final String SCREENSHOTS_DIR = "testReports/screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    // Private constructor to prevent direct instantiation
    private ScreenshotUtils() {}

    /**
     * Captures a full-page screenshot and saves it as a PNG in reports/screenshots/.
     *
     * @param driver   The active WebDriver instance
     * @param testName The name of the test or descriptive label for the file
     * @return Absolute file path of the saved screenshot, or empty string on failure
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            System.err.println("[ScreenshotUtils] Cannot capture screenshot: WebDriver instance is null.");
            return "";
        }

        try {
            // Ensure target directory exists
            Path directoryPath = Paths.get(System.getProperty("user.dir"), SCREENSHOTS_DIR);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // Sanitize filename and append unique timestamp
            String safeTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String fileName = String.format("%s_%s.png", safeTestName, timestamp);
            Path targetPath = directoryPath.resolve(fileName);

            // Take screenshot and move file to destination
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return targetPath.toAbsolutePath().toString();

        } catch (IOException e) {
            System.err.println("[ScreenshotUtils] Failed to save screenshot: " + e.getMessage());
            return "";
        } catch (Exception e) {
            System.err.println("[ScreenshotUtils] Error while taking screenshot: " + e.getMessage());
            return "";
        }
    }

    /**
     * Captures a screenshot as a Base64 string for direct embedding into HTML reports.
     *
     * @param driver The active WebDriver instance
     * @return Base64 encoded string of the screenshot, or empty string on failure
     */
    public static String captureScreenshotAsBase64(WebDriver driver) {
        if (driver == null) {
            System.err.println("[ScreenshotUtils] Cannot capture Base64 screenshot: WebDriver is null.");
            return "";
        }
        try {
            String base64Image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            return base64Image;
        } catch (Exception e) {
            System.err.println("[ScreenshotUtils] Error capturing Base64 screenshot: " + e.getMessage());
            return "";
        }
    }
}