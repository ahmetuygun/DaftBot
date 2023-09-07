package bot;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostApply {

    static String currentFileName = "Dublin1694084991100.txt";
    private static final String FORM_NAME = "Ahmet Uygun";
    private static final String FORM_EMAIL = "ygnhmt@gmail.com";
    private static final String FORM_PHONE = "0851066672";


    private static final String FORM_MESSAGE = "Hello\n" +
            "I'm writing to inquire about the availability of the property. I'm keen to learn more about it and would appreciate the opportunity to schedule a viewing. I plan to move with my friend.\n" +
            "We are both in our thirties and work as senior software engineers for an Irish IT company. We are happy to provide company and landlord reference letters, along with bank statements, to demonstrate our financial capability.\n" +
            "If you could kindly provide me with additional details about the property and confirm its availability, I would greatly appreciate it.\n" +
            "I'm looking forward to hearing back from you soon. \n" +
            "Thank you.\n" +
            "Ahmet\n";

    static String dontApplyFileName = "dontApply.txt";

    static final String ALREADY_APPLIED = "ALREADY_APPLIED";
    static final String OTHER = "OTHER";
    static final String SUCCESS = "SUCCESS";

    private static final Logger LOGGER = Logger.getLogger(PostApply.class.getName());

    public static void main(String[] args) throws  IOException {

        WebDriver driver = WebDriverInitializer.initializeAndLogin();

        Stream<String> jobs = Files.lines(Paths.get(currentFileName));
        List<String> dontApply = Files.lines(Paths.get(dontApplyFileName)).collect(Collectors.toList());
        List exclude = Arrays.asList(ALREADY_APPLIED,SUCCESS);

        jobs.filter(item -> (item.split(",").length == 1) || (item.split(",").length > 1 && !exclude.contains(item.split(",")[1])))
                .map(item -> item.split(",")[0])
                .forEach(jobId -> {
                    if(!dontApply.contains(jobId)){
                        behaveLikeAHuman(10);
                        String result = applyByLink(jobId, driver);
                        try {
                            updateStatus(jobId, result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            behaveLikeAHuman(3);
                        };
                    }

                });
    }


    public static String applyByLink(String link, WebDriver driver) {

        LOGGER.info("applying: " + link);
        driver.get(link);

        behaveLikeAHuman(15);
        try {
            WebElement easyApply = null;
            try {
                WebDriverWait wait = new WebDriverWait(driver, 10);
                easyApply = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@data-tracking='email-btn']")));
            } catch (Exception e) {
                return OTHER;
            }
            boolean keep = true;
            easyApply.click();
            behaveLikeAHuman(8);
            WebElement name = driver.findElement(By.xpath("//input[@name='name']"));
            name.sendKeys(FORM_NAME);

            WebElement email = driver.findElement(By.xpath("//input[@name='email']"));
            email.sendKeys(FORM_EMAIL);

            WebElement phone = driver.findElement(By.xpath("//input[@name='phone']"));
            phone.sendKeys(FORM_PHONE);

            WebElement message = driver.findElement(By.xpath("//textarea[@name='message']"));
            String textMessagesFormatted = FORM_MESSAGE.replace("\n", Keys.chord(Keys.SHIFT, Keys.ENTER));

            message.sendKeys(FORM_MESSAGE);

            WebElement send = driver.findElement(By.xpath("//button[@aria-label='Send']"));
            send.click();

        } catch (Exception e) {
            e.printStackTrace();
            return OTHER;
        }

        return SUCCESS;
    }
    private static void updateStatus(String jobId, String result) throws IOException {

        List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(currentFileName), StandardCharsets.UTF_8));

        for (int i = 0; i < fileContent.size(); i++) {
            if (fileContent.get(i).split(",")[0].equals(jobId)) {
                fileContent.set(i, jobId + "," + result);
                break;
            }
        }

        Files.write(Paths.get(currentFileName), fileContent, StandardCharsets.UTF_8);
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
