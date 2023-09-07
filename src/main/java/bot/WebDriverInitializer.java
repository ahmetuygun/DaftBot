package bot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverInitializer {

    static String user = "**";
    static String password = "***";
    public static WebDriver initializeAndLogin() {

        System.setProperty("webdriver.gecko.driver","drivers\\geckodriver.exe");
        WebDriver driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.get("https://www.daft.ie/auth/authenticate");

        WebElement username = driver.findElement(By.id("username"));
        WebElement pazzword = driver.findElement(By.id("password"));
        WebElement login = driver.findElement(By.className("login__button"));

        username.sendKeys(user);
        pazzword.sendKeys(password);
        login.click();

        return driver;
    }
}
