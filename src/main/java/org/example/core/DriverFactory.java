package org.example.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.nio.file.Paths;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Private constructor to prevent instantiation
    private DriverFactory() {
    }

    /**
     * Initializes a WebDriver instance based on the browser name and headless flag.
     *
     * @param browser  Target browser (chrome, firefox, edge)
     * @param headless Whether to launch in headless mode
     */
    public static void initDriver(String browser, boolean headless) {
        if (driver.get() == null) {
            String browserName = (browser != null) ? browser.trim().toLowerCase() : "chrome";
            WebDriver driver;

            switch (browserName) {
                case "chrome":
                    setDriverExecutable("webdriver.chrome.driver", "chromedriver");
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if (headless) {
                        chromeOptions.addArguments("--headless=new");
                    }
                    chromeOptions.addArguments("--start-maximized");
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    driver = new ChromeDriver(chromeOptions);
                    if (!headless) {
                        driver.manage().window().maximize();
                    }
                    break;

                case "firefox":
                    setDriverExecutable("webdriver.gecko.driver", "geckodriver");
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (headless) {
                        firefoxOptions.addArguments("-headless");
                    }
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                    driver = new FirefoxDriver(firefoxOptions);
                    if (!headless) {
                        driver.manage().window().maximize();
                    }
                    break;

                case "edge":
                    setDriverExecutable("webdriver.edge.driver", "msedgedriver");
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (headless) {
                        edgeOptions.addArguments("--headless=new");
                    }
                    edgeOptions.addArguments("--start-maximized");
                    edgeOptions.addArguments("--disable-notifications");
                    edgeOptions.addArguments("--no-sandbox");
                    edgeOptions.addArguments("--disable-dev-shm-usage");
                    driver = new EdgeDriver(edgeOptions);
                    if (!headless) {
                        driver.manage().window().maximize();
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported browser type: " + browser);
            }

            DriverFactory.driver.set(driver);
        }
    }

    /**
     * Retrieves the thread-safe WebDriver instance for the calling thread.
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Quits the current thread's WebDriver session and cleans up resources.
     */
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }

    /**
     * Resolves the relative path to the local drivers directory and registers System property.
     * Automatically appends .exe on Windows platforms.
     */
    private static void setDriverExecutable(String propertyKey, String executableBaseName) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String fileName = isWindows ? executableBaseName + ".exe" : executableBaseName;

        String driverPath = Paths.get(System.getProperty("user.dir"), "drivers", fileName)
                .toAbsolutePath()
                .toString();

        File driverFile = new File(driverPath);
        if (!driverFile.exists()) {
            throw new IllegalStateException("Offline driver binary not found at: " + driverPath);
        }

        // Ensure binary execution permissions on Unix environments
        if (!isWindows && !driverFile.canExecute()) {
            driverFile.setExecutable(true);
        }

        System.setProperty(propertyKey, driverPath);
    }
}