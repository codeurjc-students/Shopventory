package es.codeurjc.shopventory.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginSeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void loginPage_isAccessible() {
        driver.get("http://localhost:" + port + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        assertTrue(driver.getPageSource().length() > 0);
    }

    @Test
    void loginForm_withWrongCredentials_showsError() {
        driver.get("http://localhost:" + port + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='email'], input[formControlName='username']")));

        WebElement emailInput = driver.findElement(
                By.cssSelector("input[type='email'], input[formControlName='username']"));
        WebElement passwordInput = driver.findElement(
                By.cssSelector("input[type='password']"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        emailInput.sendKeys("wrong@test.com");
        passwordInput.sendKeys("wrongpassword");
        submitButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-danger")));
        WebElement errorAlert = driver.findElement(By.cssSelector(".alert-danger"));
        assertNotNull(errorAlert);
        assertFalse(errorAlert.getText().isEmpty());
    }

    @Test
    void registerPage_isAccessible() {
        driver.get("http://localhost:" + port + "/register");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        assertTrue(driver.getPageSource().length() > 0);
    }
}
