package uk.org.adacollege.apprenticeship.testing;

import static org.junit.Assert.assertTrue;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Function;

public class SolutionIT {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String startUrl;
    private static String myWhipbirdsMenuId = "my-whipbirds-menu";
    private static String aboutMenuId = "about-menu";
    private static String logOutMenuId = "log-out-menu";
    private static String logInMenuId = "log-in-menu";
    private static String emailInputId = "email";
    private static String passwordInputId = "password";
    private static String validEmail = "shannon.sullivan@adacollege.org.uk";
    private static String invalidEmail = validEmail + ".nothing";
    private static String validPassword = "whipit";
    private static String invalidPassword = validPassword + "-invalid";
    private static String logInButtonId = "login-button";
    private static String logOutButtonId = "log-out-button";
    private static String popupMessageId = "global-snackbar";

    // ========= UTILITY METHODS =========

    /**
     * Source & usage: https://stackoverflow.com/a/5709805
     */
    private static Function<WebDriver, WebElement> presenceOfElementLocated(final By locator) {
        return new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        };
    }

    private static void logIn(Boolean withValidCredentials) {
        String email = withValidCredentials ? validEmail : invalidEmail;
        String password = withValidCredentials ? validPassword : invalidPassword;

        wait.until(presenceOfElementLocated(By.id(logInMenuId)));
        driver.findElement(By.id(logInMenuId)).click();

        wait.until(presenceOfElementLocated(By.id(emailInputId)));
        driver.findElement(By.id(emailInputId)).sendKeys(email);

        wait.until(presenceOfElementLocated(By.id(passwordInputId)));
        driver.findElement(By.id(passwordInputId)).sendKeys(password);

        wait.until(presenceOfElementLocated(By.id(logInButtonId)));
        driver.findElement(By.id(logInButtonId)).click();

        if (withValidCredentials) {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.getTitle().equals("whipbird: my whipbirds");
                }
            });
        }
    }

    private static void logOut() {
        Boolean isLoggedIn = (driver.findElements(By.id(logOutMenuId)).size() > 0);

        if (isLoggedIn) {
            wait.until(presenceOfElementLocated(By.id(logOutMenuId)));
            driver.findElement(By.id(logOutMenuId)).click();

            wait.until(presenceOfElementLocated(By.id(logOutButtonId)));
            driver.findElement(By.id(logOutButtonId)).click();
        }
    }

    private static void assertElementPresent(String elementId) {
        wait.until(presenceOfElementLocated(By.id(elementId)));
        assertTrue(driver.findElements(By.id(elementId)).size() == 1);
    }

    private static void assertElementNotPresent(String elementId) {
        assertTrue(driver.findElements(By.id(elementId)).size() == 0);
    }

    private static void assertTitleEquals(String expectedTitle) {
        Boolean result = wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getTitle().equals(expectedTitle);
            }
        });
        assertTrue(result);
    }

    private static void assertUrlEquals(String expectedUrl) {
        Boolean result = wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getCurrentUrl().equals(expectedUrl);
            }
        });
        assertTrue(result);
    }

    private static void assertElementTextEquals(By selector, String expectedText) {
        Boolean result = wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.findElement(selector).getText().equals(expectedText);
            }
        });
        assertTrue(result);
    }

    private static void deleteAllWhipbirds() {
        for (WebElement element : driver.findElements(By.className("delete-whipbird-button"))) {
            element.click();
        }
    }

    // ========= SCAFFOLDING =========

    @BeforeClass
    public static void beforeAll() {
        startUrl = "http://whipbird.mattcalthrop.com/";
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 5);
    }

    @AfterClass
    public static void afterAll() {
        driver.close();
        driver.quit();
    }

    @Before
    public void beforeEach() {
        driver.get(startUrl);
    }

    @After
    public void afterEach() {
        logOut();
    }

    // ========= TESTS =========

    // --------- WHEN NOT LOGGED IN ---------

    // Step 1
    @Test
    public void notLoggedIn_checkMenus() {
        assertElementPresent(logInMenuId);
        assertElementPresent(aboutMenuId);
        assertElementNotPresent(logOutMenuId);
        assertElementNotPresent(myWhipbirdsMenuId);
    }

    // Step 2
    @Test
    public void notLoggedIn_checkCurrentPage() {
        assertUrlEquals(startUrl + "#!/login");
        assertTitleEquals("whipbird: log in");
        assertElementTextEquals(By.tagName("h4"), "Log in");
        assertElementTextEquals(By.id("footer-right"), "");
    }

    // Step 3
    @Test
    public void notLoggedIn_clickAboutMenu() {
        // Click on the about menu item first
        wait.until(presenceOfElementLocated(By.id(aboutMenuId)));
        driver.findElement(By.id(aboutMenuId)).click();

        assertUrlEquals(startUrl + "#!/about");
        assertTitleEquals("whipbird: about");
        assertElementTextEquals(By.tagName("h4"), "About this app");
    }

    // Step 4
    @Test
    public void notLoggedIn_logInWithIncorrectCredentials() {
        logIn(false);
        assertElementPresent(logInMenuId);
        assertElementPresent(aboutMenuId);
        assertElementNotPresent(logOutMenuId);
        assertElementNotPresent(myWhipbirdsMenuId);
        assertUrlEquals(startUrl + "#!/login");
        assertTitleEquals("whipbird: log in");
        assertElementTextEquals(By.id(popupMessageId), "Username or password incorrect");
        assertElementTextEquals(By.id("footer-right"), "");
    }

    // --------- WHEN LOGGED IN ---------

    // Step 5
    @Test
    public void loggedIn_checkMenus() {
        logIn(true);
        assertElementPresent(aboutMenuId);
        assertElementPresent(logOutMenuId);
        assertElementPresent(myWhipbirdsMenuId);
        assertElementNotPresent(logInMenuId);
    }

    // Step 6
    @Test
    public void loggedIn_checkCurrentPage() {
        logIn(true);
        assertUrlEquals(startUrl + "#!/my-whipbirds");
        assertTitleEquals("whipbird: my whipbirds");
        assertElementTextEquals(By.tagName("h4"), "Current whipbirds for Shannon Sullivan");
        assertElementTextEquals(By.id("footer-right"), "Shannon Sullivan");
    }

    // Step 7
    @Test
    public void loggedIn_clickLogOutMenu() {
        logIn(true);
        wait.until(presenceOfElementLocated(By.id(logOutMenuId)));
        driver.findElement(By.id(logOutMenuId)).click();

        assertUrlEquals(startUrl + "#!/logout");
        assertTitleEquals("whipbird: log out");
        assertElementTextEquals(By.tagName("h4"), "Log out");
    }

    // Step 8
    @Test
    public void loggedIn_addNewWhipbird() {
        logIn(true);
        deleteAllWhipbirds();

        wait.until(presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Mavis");

        wait.until(presenceOfElementLocated(By.id("age")));
        driver.findElement(By.id("age")).sendKeys("10");

        wait.until(presenceOfElementLocated(By.id("add-new-whipbird-button")));
        driver.findElement(By.id("add-new-whipbird-button")).click();

        assertElementTextEquals(By.id(popupMessageId), "Whipbird added: Mavis");

        wait.until(presenceOfElementLocated(By.id("whipbird-name-0")));
        driver.findElement(By.id("whipbird-name-0"));
        assertElementTextEquals(By.id("whipbird-name-0"), "Mavis");

        wait.until(presenceOfElementLocated(By.id("whipbird-age-0")));
        driver.findElement(By.id("whipbird-age-0"));
        assertElementTextEquals(By.id("whipbird-age-0"), "10");
    }

    // Step 9
    @Test
    public void loggedIn_addNewWhipbirdThenDeleteIt() {
        logIn(true);
        deleteAllWhipbirds();

        // Create a new whipbird
        wait.until(presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Travis");

        wait.until(presenceOfElementLocated(By.id("age")));
        driver.findElement(By.id("age")).sendKeys("13");

        wait.until(presenceOfElementLocated(By.id("add-new-whipbird-button")));
        driver.findElement(By.id("add-new-whipbird-button")).click();

        assertElementTextEquals(By.id(popupMessageId), "Whipbird added: Travis");

        wait.until(presenceOfElementLocated(By.id("whipbird-name-0")));
        driver.findElement(By.id("whipbird-name-0"));
        assertElementTextEquals(By.id("whipbird-name-0"), "Travis");

        wait.until(presenceOfElementLocated(By.id("whipbird-age-0")));
        driver.findElement(By.id("whipbird-age-0"));
        assertElementTextEquals(By.id("whipbird-age-0"), "13");

        // Delete the whipbird
        wait.until(presenceOfElementLocated(By.id("delete-whipbird-button-0")));
        driver.findElement(By.id("delete-whipbird-button-0")).click();

        assertElementPresent("no-whipbirds-saved");
    }
}