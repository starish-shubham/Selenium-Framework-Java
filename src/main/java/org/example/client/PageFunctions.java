package org.example.client;

import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.reports.ExtentTestManager;
import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class PageFunctions {

    protected WebDriver driver;
    protected WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    private static final Logger log = LogManager.getLogger(PageFunctions.class);


    public PageFunctions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
    }

    public PageFunctions(WebDriver driver, int timeoutInSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    }

    // ==========================================
    // 1. Click Actions
    // ==========================================

    public void clickElementByXpath(String xpath) {
        logInfo("Clicked on element: " + xpath);
        if (xpath == null || xpath.trim().isEmpty()) {
            throw new IllegalArgumentException("XPath expression cannot be null or empty.");
        }

        By locator = By.xpath(xpath);
        try {
            log.info("Clicking element by XPath: {}", xpath);
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
            log.info("Successfully clicked element by XPath: {}", xpath);
        } catch (TimeoutException e) {
            captureScreenshot("click_timeout_" + xpath.replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Element not clickable for XPath: {}", xpath, e);
            throw new RuntimeException("Element not clickable for XPath: " + xpath, e);
        } catch (NoSuchElementException e) {
            captureScreenshot("click_notfound_" + xpath.replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Element not found for XPath: {}", xpath, e);
            throw new RuntimeException("Element not found for XPath: " + xpath, e);
        } catch (Exception e) {
            captureScreenshot("click_error_" + xpath.replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Unexpected error while clicking XPath: {}", xpath, e);
            throw new RuntimeException("Failed to click element for XPath: " + xpath, e);
        }
    }

    /**
     * Automatic Screenshot Utility. Saves PNG image to ./screenshots/ directory.
     */
    public String captureScreenshot(String screenshotName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timeStamp + ".png";
        String destPath = System.getProperty("user.dir") + "/screenshots/" + fileName;

        File destination = new File(destPath);
        try {
            FileHandler.copy(source, destination);
            log.info("[SCREENSHOT SAVED]: Path: "+ destPath);
        } catch (IOException e) {
            log.error("Failed to save screenshot: {}", e.getMessage());
        }
        return destPath;
    }

    public void type(String locator, String textToType) {
        if (textToType == null) {
            throw new IllegalArgumentException("Text input cannot be null.");
        }

        By Xpath = By.xpath(locator);
        try {
            log.info("Typing text '{}' into element: {}", textToType, locator);

            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(Xpath));
            element.sendKeys(textToType);

            log.info("Successfully typed text into element: {}", Xpath);
        } catch (TimeoutException e) {
            captureScreenshot("type_timeout_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Timeout waiting for element to be visible: {}", Xpath, e);
            throw new RuntimeException("Timeout waiting for element to be visible: " + Xpath, e);
        } catch (NoSuchElementException e) {
            captureScreenshot("type_notfound_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Element not found for typing: {}", Xpath, e);
            throw new RuntimeException("Element not found for typing: " + Xpath, e);
        } catch (ElementNotInteractableException e) {
            captureScreenshot("type_not_interactable_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Element not interactable for typing: {}", Xpath, e);
            throw new RuntimeException("Element not interactable for typing: " + Xpath, e);
        } catch (Exception e) {
            captureScreenshot("type_error_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Unexpected error while typing into element: {}", Xpath, e);
            throw new RuntimeException("Failed to type into element: " + Xpath, e);
        }
    }

    public void clear(String locator) {
        By Xpath = By.xpath(locator);
        try {
            log.info("Clearing text from element: {}", locator);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(Xpath));
            element.clear();

            log.info("Successfully cleared element: {}", Xpath);
        } catch (TimeoutException e) {
            captureScreenshot("clear_timeout_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Timeout waiting for element to be visible: {}", Xpath, e);
            throw new RuntimeException("Timeout waiting for element to be visible: " + locator, e);
        } catch (NoSuchElementException e) {
            captureScreenshot("clear_notfound_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Element not found for clearing: {}", Xpath, e);
            throw new RuntimeException("Element not found for clearing: " + Xpath, e);
        } catch (ElementNotInteractableException e) {
            captureScreenshot("clear_not_interactable_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Element not interactable for clearing: {}", Xpath, e);
            throw new RuntimeException("Element not interactable for clearing: " + Xpath, e);
        } catch (Exception e) {
            captureScreenshot("clear_error_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Unexpected error while clearing element: {}", Xpath, e);
            throw new RuntimeException("Failed to clear element: " + Xpath, e);
        }
    }

    public String getText(String locator) {
        By Xpath = By.xpath(locator);
        try {
            log.info("Retrieving text from element: {}", locator);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(Xpath));
            String text = element.getText().trim();

            log.info("Successfully retrieved text '{}' from element: {}", text, Xpath);
            return text;
        } catch (TimeoutException e) {
            captureScreenshot("getText_timeout_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Timeout waiting for element to be visible: {}", Xpath, e);
            throw new RuntimeException("Timeout waiting for element to be visible: " + Xpath, e);
        } catch (NoSuchElementException e) {
            captureScreenshot("getText_notfound_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Element not found for retrieving text: {}", Xpath, e);
            throw new RuntimeException("Element not found for retrieving text: " + Xpath, e);
        } catch (Exception e) {
            captureScreenshot("getText_error_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Unexpected error while retrieving text from element: {}", Xpath, e);
            throw new RuntimeException("Failed to retrieve text from element: " + Xpath, e);
        }
    }

    public String getAttribute(String locator, String attributeName) {
        By Xpath = By.xpath(locator);
        try {
            log.info("Retrieving attribute '{}' from element: {}", attributeName, locator);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(Xpath));
            String attributeValue = element.getAttribute(attributeName);

            log.info("Successfully retrieved attribute '{}' with value '{}' from element: {}", attributeName, attributeValue, Xpath);
            return attributeValue;
        } catch (TimeoutException e) {
            captureScreenshot("getAttribute_timeout_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Timeout waiting for element to be visible: {}", Xpath, e);
            throw new RuntimeException("Timeout waiting for element to be visible: " + Xpath, e);
        } catch (NoSuchElementException e) {
            captureScreenshot("getAttribute_notfound_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Element not found for retrieving attribute: {}", Xpath, e);
            throw new RuntimeException("Element not found for retrieving attribute: " + Xpath, e);
        } catch (Exception e) {
            captureScreenshot("getAttribute_error_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Unexpected error while retrieving attribute '{}' from element: {}", attributeName, Xpath, e);
            throw new RuntimeException("Failed to retrieve attribute from element: " + Xpath, e);
        }
    }

    public boolean isDisplayed(String locator) {
        By Xpath = By.xpath(locator);
        try {
            log.info("Checking if element is displayed: {}", locator);
            boolean isVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(Xpath)).isDisplayed();

            log.info("Element display status for {}: {}", Xpath, isVisible);
            return isVisible;
        } catch (TimeoutException e) {
            log.warn("Element not displayed within timeout: {}", Xpath);
            return false;
        } catch (NoSuchElementException e) {
            log.warn("Element not found in DOM: {}", Xpath);
            return false;
        } catch (Exception e) {
            captureScreenshot("isDisplayed_error_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Unexpected error while checking display status for element: {}", Xpath, e);
            throw new RuntimeException("Failed to check display status for element: " + Xpath, e);
        }
    }

    public boolean isEnabled(String locator) {
        By Xpath = By.xpath(locator);
        try {
            log.info("Checking if element is enabled: {}", locator);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(Xpath));
            boolean enabled = element.isEnabled();

            log.info("Element enabled status for {}: {}", Xpath, enabled);
            return enabled;
        } catch (TimeoutException e) {
            log.warn("Element not present within timeout: {}", Xpath);
            return false;
        } catch (NoSuchElementException e) {
            log.warn("Element not found in DOM: {}", Xpath);
            return false;
        } catch (Exception e) {
            captureScreenshot("isEnabled_error_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Unexpected error while checking enabled status for element: {}", Xpath, e);
            throw new RuntimeException("Failed to check enabled status for element: " + Xpath, e);
        }
    }

    public boolean isSelected(String locator) {
        By Xpath = By.xpath(locator);
        try {
            log.info("Checking if element is selected: {}", locator);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(Xpath));
            boolean selected = element.isSelected();

            log.info("Element selected status for {}: {}", Xpath, selected);
            return selected;
        } catch (TimeoutException e) {
            log.warn("Element not present within timeout: {}", Xpath);
            return false;
        } catch (NoSuchElementException e) {
            log.warn("Element not found in DOM: {}", Xpath);
            return false;
        } catch (Exception e) {
            captureScreenshot("isSelected_error_" + Xpath.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            log.error("Unexpected error while checking selected status for element: {}", Xpath, e);
            throw new RuntimeException("Failed to check selected status for element: " + Xpath, e);
        }
    }


    /**
     * Simultaneously writes to Log4j2 (console/file) and the active thread's Extent Test.
     */
    protected void logInfo(String message) {
        log.info(message);
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.INFO, message);
        }
    }

    protected void logFail(String message) {
        log.error(message);
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.FAIL, message);
        }
    }

}