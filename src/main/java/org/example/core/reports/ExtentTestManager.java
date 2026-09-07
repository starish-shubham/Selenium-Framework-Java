package org.example.core.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> extentTestThread = new ThreadLocal<>();
    private static final ExtentReports extent = ExtentManager.getInstance();

    public static ExtentTest getTest() {
        return extentTestThread.get();
    }

    public static synchronized ExtentTest startTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        extentTestThread.set(test);
        return test;
    }

    public static void unload() {
        extentTestThread.remove(); // Prevent memory leaks
    }
}