package es.codeurjc.shopventory.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/** Basic end-to-end flows around the login page. */
class LoginSeleniumTest extends AbstractSeleniumTest {

    private static final By USERNAME = By.cssSelector("input[formControlName='username']");
    private static final By PASSWORD = By.cssSelector("input[formControlName='password']");
    private static final By SUBMIT = By.cssSelector("button[type='submit']");

    @Test
    void loginPage_rendersLoginForm() {
        driver.get(url("/login"));

        wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME));
        assertTrue(driver.findElement(PASSWORD).isDisplayed());
        assertTrue(driver.findElement(SUBMIT).isDisplayed());
    }

    @Test
    void loginForm_withWrongCredentials_showsError() {
        driver.get(url("/login"));
        wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME));

        driver.findElement(USERNAME).sendKeys("wrong@test.com");
        driver.findElement(PASSWORD).sendKeys("wrongpassword");
        driver.findElement(SUBMIT).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger")));
        assertFalse(error.getText().isBlank());
    }

    @Test
    void loginPage_linkNavigatesToRegister() {
        driver.get(url("/login"));

        WebElement registerLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Register")));
        registerLink.click();

        wait.until(ExpectedConditions.urlContains("/register"));
        assertTrue(driver.getCurrentUrl().contains("/register"));
    }
}
