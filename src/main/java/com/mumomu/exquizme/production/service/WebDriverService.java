package com.mumomu.exquizme.production.service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebDriverService {
    private WebDriver driver;
    public static String WEB_DRIVER_ID = "webdriver.chrome.driver"; // Properties 설정
    //TODO : WebDriver 경로 상대경로로 바꾸기
    public static String WEB_DRIVER_PATH = "/Users/minkyumkim/Desktop/chromedriver"; // WebDriver 경로

    public WebDriverService() {
        chrome();
    }

    private void chrome() {
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        // webDriver 옵션 설정.
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("--lang=ko");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.setCapability("ignoreProtectedModeSettings", true);

        // webDriver 생성.
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
    }

    public List<String> process(String url) {
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);

        List<String> res = new ArrayList<>();

        List<WebElement> webElements = driver.findElements(By.cssSelector(".rg_i.Q4LuWd"));

        for (WebElement webElement : webElements) {
            webElement.click();

            res.add(webElement.toString());
        }

        quitDriver();

        return res;
    }

    public void useDriver(String url) {
        driver.get(url) ;
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);  // 페이지 불러오는 여유시간.
        log.info("++++++++++++++++++++++===================+++++++++++++ selenium : " + driver.getTitle());

        WebElement searchLabel = driver.findElement(By.id("label-text"));
        log.info("++++++++++++++++++++++===================+++++++++++++ searchLabel : " + searchLabel.getText());

        quitDriver();
    }

    private void quitDriver() {
        driver.quit(); // webDriver 종료
    }

}
