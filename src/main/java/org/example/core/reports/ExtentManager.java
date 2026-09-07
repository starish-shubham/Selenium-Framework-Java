package org.example.core.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport.html";
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("Test Execution Report");
            spark.config().setReportName("Automation Test Results");

            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
        return extent;
    }

    /**
     * Appends dynamic key-value properties to the Extent dashboard Environment table.
     */
    public static synchronized void setSystemInfo(String key, String value) {
        if (value != null && !value.isEmpty()) {
            getInstance().setSystemInfo(key, value);
        }
    }
}