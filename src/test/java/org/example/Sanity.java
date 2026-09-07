package org.example;

import org.example.core.BaseTest;
import org.example.core.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

public class Sanity extends BaseTest {

    private String baseURI;
    private String env;

    @BeforeTest(alwaysRun = true)
    public void setupClass(ITestContext context) {
        params = context.getCurrentXmlTest().getAllParameters();
        baseURI = ConfigReader.get("qa.baseURI");
        env = params.get("environment");
    }

    @Test(priority = 1, groups = {"sanity1"})
    public void Test1() {
        System.out.println("Browser: " + params.get("browser"));
        System.out.println("Environment: " + env);
        System.out.println("Sanity Test 1");
        System.out.println("Base URI: " + baseURI);
    }
}