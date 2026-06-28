package es.codeurjc.shopventory.selenium;

import es.codeurjc.shopventory.model.User;
import es.codeurjc.shopventory.repository.UserRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
@ActiveProfiles("test")
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

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox",
                "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1280,1024");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        seedUsers();
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** True when the built Angular index.html is present on the classpath. */
    protected static boolean isFrontendBuilt() {
        return AbstractSeleniumTest.class.getClassLoader().getResource("static/index.html") != null;
    }

    protected String url(String path) {
        return "http://localhost:" + port + path;
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
