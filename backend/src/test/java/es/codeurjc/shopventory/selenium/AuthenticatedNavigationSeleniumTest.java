package es.codeurjc.shopventory.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Authenticated end-to-end flows (run over HTTPS so the Secure JWT cookies
 * persist): successful login, role-based navigation menu and admin-only route
 * protection.
 */
class AuthenticatedNavigationSeleniumTest extends AbstractSeleniumTest {

    private static final By PRODUCTS_LINK = By.cssSelector("a[href='/products']");
    private static final By USERS_LINK = By.cssSelector("a[href='/users']");
    private static final By EMPLOYEES_LINK = By.cssSelector("a[href='/employees']");

    @Test
    void login_withValidAdminCredentials_reachesDashboard() {
        loginAs("admin@shopventory.com", "Admin1234!");

        assertTrue(driver.getCurrentUrl().contains("/dashboard"));
        // The navbar (only rendered for authenticated users) is present.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".navbar-brand")));
    }

    @Test
    void adminUser_seesAdminMenuItems() {
        loginAs("admin@shopventory.com", "Admin1234!");

        // Admin-only entries are present in the navbar.
        wait.until(ExpectedConditions.presenceOfElementLocated(USERS_LINK));
        assertFalse(driver.findElements(EMPLOYEES_LINK).isEmpty());
        assertFalse(driver.findElements(PRODUCTS_LINK).isEmpty());
    }

    @Test
    void standardUser_doesNotSeeAdminMenuItems() {
        loginAs("user@shopventory.com", "User1234!");

        // Sync on a link every logged-in user sees, then assert admin ones are absent.
        wait.until(ExpectedConditions.presenceOfElementLocated(PRODUCTS_LINK));
        assertTrue(driver.findElements(USERS_LINK).isEmpty());
        assertTrue(driver.findElements(EMPLOYEES_LINK).isEmpty());
    }

    @Test
    void standardUser_directNavigationToUsers_redirectsToForbidden() {
        loginAs("user@shopventory.com", "User1234!");

        driver.get(url("/users"));

        wait.until(ExpectedConditions.urlContains("/403"));
        assertTrue(driver.getCurrentUrl().contains("/403"));
    }
}
