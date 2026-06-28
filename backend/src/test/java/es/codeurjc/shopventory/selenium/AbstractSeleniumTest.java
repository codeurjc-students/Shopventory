package es.codeurjc.shopventory.selenium;

import es.codeurjc.shopventory.model.User;
import es.codeurjc.shopventory.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

/**
 * Base for end-to-end Selenium tests. Spins up the full application on a random
 * port and drives the real Angular SPA (served from the backend's static
 * resources) with a headless Chrome browser.
 *
 * Two prerequisites are handled here:
 *  - The Angular app must be built into src/main/resources/static. When it is
 *    not (e.g. a plain "mvn test" without the frontend build) there is no UI to
 *    drive, so each test is skipped cleanly instead of failing.
 *  - The DataInitializer is disabled under the "test" profile, so the canonical
 *    users are seeded here for the login-related flows.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "selenium"})
abstract class AbstractSeleniumTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpDriver() {
        Assumptions.assumeTrue(isFrontendBuilt(),
                "Angular SPA not found in static/ — build the frontend first "
                        + "(skipping Selenium UI tests).");

        driver = createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        seedUsers();
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Creates a headless browser. Prefers Edge; falls back to Chrome (both are
     * Chromium-based and share the same flags), so the tests run on machines
     * that have either one installed. The driver binary is resolved
     * automatically by Selenium Manager (built into Selenium 4.x).
     */
    private WebDriver createDriver() {
        String[] flags = {"--headless=new", "--no-sandbox",
                "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1280,1024"};
        try {
            EdgeOptions options = new EdgeOptions();
            options.addArguments(flags);
            options.setAcceptInsecureCerts(true); // self-signed cert over HTTPS
            return new EdgeDriver(options);
        } catch (Exception edgeUnavailable) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(flags);
            options.setAcceptInsecureCerts(true);
            return new ChromeDriver(options);
        }
    }

    /** True when the built Angular index.html is present on the classpath. */
    protected static boolean isFrontendBuilt() {
        return AbstractSeleniumTest.class.getClassLoader().getResource("static/index.html") != null;
    }

    protected String url(String path) {
        // HTTPS so the Secure JWT cookies are kept by the browser (the "selenium"
        // profile enables SSL). The self-signed cert is accepted by the driver.
        return "https://localhost:" + port + path;
    }

    private static final By USERNAME_INPUT = By.cssSelector("input[formControlName='username']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[formControlName='password']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");

    /** Logs in through the real login form and waits until the dashboard is reached. */
    protected void loginAs(String email, String password) {
        driver.get(url("/login"));
        wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME_INPUT));
        driver.findElement(USERNAME_INPUT).sendKeys(email);
        driver.findElement(PASSWORD_INPUT).sendKeys(password);
        driver.findElement(SUBMIT_BUTTON).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    /** Seeds the canonical admin and standard users (DataInitializer is off under "test"). */
    protected void seedUsers() {
        if (!userRepository.existsByEmail("admin@shopventory.com")) {
            User admin = new User("admin@shopventory.com",
                    passwordEncoder.encode("Admin1234!"), "Carlos", "Garcia");
            admin.setRoles(List.of("ADMIN", "USER"));
            admin.setApproved(true);
            userRepository.save(admin);
        }
        if (!userRepository.existsByEmail("user@shopventory.com")) {
            User user = new User("user@shopventory.com",
                    passwordEncoder.encode("User1234!"), "Laura", "Martinez");
            user.setRoles(List.of("USER"));
            user.setApproved(true);
            userRepository.save(user);
        }
    }
}
