import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.MainPage;
import pages.OrderPage;
import pages.PopUpWindow;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.time.Duration;

import static org.junit.Assert.assertTrue;

public class PopUpWindowTest {
    private WebDriver driver;
    private MainPage mainPage;
    private OrderPage orderPage;
    private PopUpWindow popUpWindow;


    private String name = "Иван";
    private String surname = "Иванов";
    private String address = "Пушкина, 1";
    private String phone = "+79000000000";
    private String rentPeriod = "сутки";
    private String color = "black";
    private String comment = "Комментарий";


    @Before
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        mainPage = new MainPage();
        orderPage = new OrderPage();
        popUpWindow = new PopUpWindow();
        driver.get("https://qa-scooter.praktikum-services.ru/");
        closeCookies();
    }

    @Test
    public void shouldDisplayConfirmationPopup() {
        driver.findElement(mainPage.orderButtonTop).click();
        fillOrderForm();


        String confirmationText = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(popUpWindow.confirmationMessage)).getText();
        assertTrue("Сообщение о создании заказа не найдено!", confirmationText.contains("Заказ оформлен"));
    }

    private void fillOrderForm() {
        driver.findElement(orderPage.nameInput).sendKeys(name);
        driver.findElement(orderPage.surnameInput).sendKeys(surname);
        driver.findElement(orderPage.addressInput).sendKeys(address);

        driver.findElement(orderPage.metroStationInput).click();
        driver.findElement(orderPage.metroStationIndex).click();

        driver.findElement(orderPage.phoneInput).sendKeys(phone);
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(orderPage.nextButton));
        driver.findElement(orderPage.nextButton).click();

        new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.elementToBeClickable(orderPage.dateInput));
        driver.findElement(orderPage.dateInput).click();
        driver.findElement(orderPage.datePick).click();

        new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.elementToBeClickable(orderPage.rentPeriodInput));
        driver.findElement(orderPage.rentPeriodInput).click();
        driver.findElement(By.xpath("//div[contains(text(), '" + rentPeriod + "')]")).click();

        if (color.equals("black")) {
            driver.findElement(orderPage.scooterColorBlack).click();
        } else {
            driver.findElement(orderPage.scooterColorGrey).click();
        }

        driver.findElement(orderPage.commentInput).sendKeys(comment);
        driver.findElement(orderPage.orderButton).click();


        new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(orderPage.yesButton)).click();
    }

    private void closeCookies() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            driver.findElement(By.xpath("//button[text()='да все привыкли']")).click();
        } catch (Exception e) {
            System.out.println("Cookie consent banner not found, proceeding with test.");
        }
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
