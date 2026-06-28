package es.codeurjc.shopventory.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/** Basic end-to-end flow for the registration page. */
class RegisterSeleniumTest extends AbstractSeleniumTest {

    @Test
    void registerPage_rendersForm() {
        driver.get(url("/register"));

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[formControlName='name']")));
        assertTrue(driver.findElement(By.cssSelector("input[formControlName='email']")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("input[formControlName='password']")).isDisplayed());
    }

    @Test
    void register_newUser_showsPendingApprovalSuccess() {
        driver.get(url("/register"));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[formControlName='name']")));

        driver.findElement(By.cssSelector("input[formControlName='name']")).sendKeys("Selenium");
        driver.findElement(By.cssSelector("input[formControlName='surname']")).sendKeys("Tester");
        driver.findElement(By.cssSelector("input[formControlName='email']"))
                .sendKeys("selenium.newuser@test.com");
        driver.findElement(By.cssSelector("input[formControlName='password']")).sendKeys("Password123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement success = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")));
        assertTrue(success.getText().toLowerCase().contains("successful"));
    }
}
