package es.codeurjc.shopventory.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Access-control flows: protected routes must bounce an unauthenticated visitor
 * to the login page (the Angular AuthGuard redirect).
 */
class AccessControlSeleniumTest extends AbstractSeleniumTest {

    @Test
    void unauthenticated_dashboard_redirectsToLogin() {
        driver.get(url("/dashboard"));

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    void unauthenticated_products_redirectsToLogin() {
        driver.get(url("/products"));

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    void unauthenticated_users_redirectsToLogin() {
        driver.get(url("/users"));

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }
}
