package org.example.core.utils;

import com.aventstack.extentreports.Status;
import org.example.core.reports.ExtentManager;
import org.example.core.reports.ExtentTestManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportLogger {

    private static final Logger log = LoggerFactory.getLogger(ReportLogger.class);

    private ReportLogger() {}

    public static void info(String message) {
        log.info(message);
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.INFO, message);
        }
    }

    public static void pass(String message) {
        log.info("[PASS] " + message);
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.PASS, message);
        }
    }

    public static void warn(String message) {
        log.warn(message);
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.WARNING, message);
        }
    }

    public static void fail(String message) {
        log.error(message);
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.FAIL, message);
        }
    }

    public static void fail(String message, Throwable throwable) {
        log.error(message, throwable);
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().fail(throwable);
        }
    }
}