package bot;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class PostSearch {

    private static final Logger LOGGER = Logger.getLogger(PostSearch.class.getName());
    private static final String LINK = "https://www.daft.ie/property-for-rent/dublin-1-dublin?showMap=false&radius=5000&rentalPrice_to=3500&rentalPrice_from=1000&numBeds_from=3&numBeds_to=4&pageSize=20";

    public static void main(String[] args) throws  IOException {

        WebDriver driver = WebDriverInitializer.initializeAndLogin();

        String fileName = "Dublin" + new Date().getTime() + ".txt";
        File myObj = new File(fileName);
        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
        } else {
            System.out.println("File already exists.");
        }

        boolean keep;

        behaveLikeAHuman(7);
        driver.get(LINK);

        do {
            keep = performByPage( fileName, driver);
            behaveLikeAHuman(7);

        } while (keep);

    }


    private static boolean performByPage( String fileName, WebDriver driver) {

        behaveLikeAHuman(5);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement  bottomElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//footer")));
        JavascriptExecutor je = (JavascriptExecutor) driver;
        je.executeScript("arguments[0].scrollIntoView(true);", bottomElement);

        List<WebElement> allProduct = null;

        try {
            allProduct = driver.findElements(By.xpath("//ul[@data-testid='results']/li"));
            LOGGER.info("Found (first glance) : " + allProduct.size() + " post.");

        } catch (Exception e) {
            return false;
        }
        if (allProduct == null || allProduct.isEmpty())
            return false;

        LOGGER.info("Found " + allProduct.size() + " post.");

        Stream<WebElement> stream = allProduct.stream();
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
            stream.map(s -> s.findElement(By.tagName("a")).getAttribute("href"))
                    .forEachOrdered(pw::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WebElement nextButton = null;

        try {

            if(driver.findElement(By.xpath("//button[@aria-label='Next >'][@disabled='']")).isDisplayed()){
                return false;
            }

        } catch (Exception e) {
            //do nothing
        }

        try {
            nextButton = driver.findElement(By.xpath("//button[@aria-label='Next >']"));
            nextButton.click();

        } catch (Exception e) {
            return false;
        }

        return true;

    }


    private static void behaveLikeAHuman(int sleepMiliSecond) {

        Random r = new Random();
        int wait = r.nextInt((sleepMiliSecond * 1000) + 1) + 1000;
        LOGGER.info("Sleeping for " + wait + " milliseconds");
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
