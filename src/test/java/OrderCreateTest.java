import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.MainPage;
import pages.OrderPage;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import static org.openqa.selenium.By.*;
// здесь добавлена проверка на успешное создание заказа т.к в браузере гугл тест не падал
//тест падает, т.к в гугле баг
@RunWith(Parameterized.class)
public class OrderCreateTest {
    private WebDriver driver;
    private MainPage mainPage;
    private OrderPage orderPage;

    private String name;
    private String surname;
    private String address;
    private String phone;
    private String rentPeriod;
    private String color;
    private String comment;

    public OrderCreateTest(String name, String surname, String address,
                           String phone,
                           String rentPeriod, String color, String comment) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.rentPeriod = rentPeriod;
        this.color = color;
        this.comment = comment;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Иван", "Иванов", "Пушкина, 1", "+79000000000",
                        "сутки", "black", "комментарий"},
                {"Петр", "Петров", "Ленина, 1", "+79000000001",
                        "двое суток", "grey", "комментарий"},
        });
    }

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        mainPage = new MainPage();
        orderPage = new OrderPage();
        driver.get("https://qa-scooter.praktikum-services.ru/");
        closeCookies();
    }

    @Test
    public void createOrderTest() {

        driver.findElement(mainPage.orderButtonTop).click();
        fillOrderForm();
        checkOrderCreation();
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

        new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(orderPage.yesButton)); //ожидание для кнопки йес
        driver.findElement(orderPage.yesButton).click();

    }

    private void closeCookies() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement cookieButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='да все привыкли']")));
            cookieButton.click();
        } catch (TimeoutException e) {
            System.out.println("Cookie consent banner not found, proceeding with test.");
        }
    }
    private void checkOrderCreation() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        try {
            WebElement orderConfirmationElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Номер заказа:')]")));


            Assert.assertNotNull("Элемент подтверждения отсутствует, заказ не был создан.", orderConfirmationElement);


            String confirmationText = orderConfirmationElement.getText();


            Assert.assertTrue("Текст подтверждения не соответствует ожиданиям.", confirmationText.contains("Номер заказа:")
                    && confirmationText.contains("Запишите его:")
                    && confirmationText.contains("пригодится, чтобы отслеживать статус"));

        } catch (TimeoutException e) {
            Assert.fail("Таймаут истек: заказ не был создан или детали заказа не видны.");
        } catch (Exception e) {
            Assert.fail("Произошла ошибка при проверке создания заказа: " + e.getMessage());
        }
    }
    @After
    public void tearDown() {
        driver.quit();
    }
}