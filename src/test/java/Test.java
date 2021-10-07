import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Test {
    WebDriver driver;
    String baseUrl;

    @Before
    public void beforeTest(){
        System.setProperty("webdriver.chrome.driver","drv/chromedriver.exe");
        baseUrl="https://www.sberbank.ru/ru/person";
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        // 1)   Перейти на страницу http://www.sberbank.ru/ru/person
        driver.get(baseUrl);
    }

    @org.junit.Test
    public void testInsurance() throws InterruptedException {

        // 2) Нажать на – Страхование
        driver.findElement(By.xpath("//*[contains(@aria-label, 'Страхование')]")).click();

        /* 3) Нажать на – Перейти в каталог
        Кнопка Перейти в каталог отутствует. В качестве альтернативы выбрана кнопка Страховые программы */
        driver.findElement(By.xpath("//*[contains(text(),'страховые программы')]")).click();

        // 4) Нажать на - Страхование путешественников
        WebElement webElement = driver.findElement(By.xpath("//*[contains(text(),'Страхование путешественников')]"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", webElement);
        webElement.click();

        // 5) Проверить наличие на странице заголовка – Страхование путешественников
        WebElement title = driver.findElement(By.xpath("//h1[contains(text(),'Страхование путешественников')]"));
        Assert.assertEquals("Страхование путешественников", title.getText());

        /* 6) Нажать на – Оформить Онлайн
        Дополнительно добавлен шаг : нажать на - Оформить на сайте.
         */
        webElement = driver.findElement(By.xpath("//span[contains(text(),'Оформить онлайн')]"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", webElement);
        webElement.click();

        webElement = driver.findElement(By.xpath("//span[contains(text(),'Оформить на сайте')]"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", webElement);
        webElement.click();

        Thread.sleep(5000);

        /* 7) На вкладке – Выбор полиса  выбрать сумму страховой защиты – Минимальная
        Для появления возможности выбора из нескольких вариантов суммы добавлен шаг с выбором другого региона
         */
        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));

       driver.findElement(By.xpath("//input-select")).click();
       driver.findElement(By.xpath("//*[contains(text(),'Все страны мира, кроме США и РФ')]")).click();

        webElement = driver.findElement(By.xpath("//h3[contains(text(),'Минимальная')]"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", webElement);
        webElement.click();
        Thread.sleep(5000);

        // 8) Нажать Оформить

        webElement = driver.findElement(By.xpath("//button[contains(text(), 'Оформить')]"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", webElement);
        webElement.click();

        /* 9) На вкладке Оформление заполнить поля:
        •       Фамилию и Имя, Дату рождения застрахованных
        •       Данные страхователя: Фамилия, Имя, Отчество, Дата рождения, Пол
        •       Паспортные данные
        •       Контактные данные не заполняем */
        fillField(By.id("surname_vzr_ins_0"), "Ivanov");
        fillField(By.id("name_vzr_ins_0"), "Ivan");
        fillField(By.id("birthDate_vzr_ins_0"), "02.02.1992");

        driver.findElement(By.id("person_lastName")).click();
        fillField(By.id("person_firstName"), "Александра");
        fillField(By.id("person_lastName"), "Александрова");
        fillField(By.id("person_middleName"), "Александровна");
        fillField(By.id("person_birthDate"), "08.12.1990");
        driver.findElement(By.id("person_firstName")).click();
        driver.findElement(By.xpath("//label[contains(text(),'Женский')]")).click();

        fillField(By.id("passportSeries"), "1111");
        fillField(By.id("passportNumber"), "111111");
        fillField(By.id("documentIssue"), "ЙЦУК №12");
        fillField(By.id("documentDate"), "01.01.2020");

        // 10) Проверить, что все поля заполнены правильно

        Assert.assertEquals("", driver.findElement(By.id("phone")).getAttribute("value"));
        Assert.assertEquals("", driver.findElement(By.id("email")).getAttribute("value"));
        Assert.assertEquals("", driver.findElement(By.id("repeatEmail")).getAttribute("value"));

        Assert.assertEquals("ЙЦУК №12", driver.findElement(By.id("documentIssue")).getAttribute("value"));
        Assert.assertEquals("01.01.2020", driver.findElement(By.id("documentDate")).getAttribute("value"));
        Assert.assertEquals("1111", driver.findElement(By.id("passportSeries")).getAttribute("value"));
        Assert.assertEquals("111111", driver.findElement(By.id("passportNumber")).getAttribute("value"));

        Assert.assertEquals("Александрова", driver.findElement(By.id("person_lastName")).
                getAttribute("value"));
        Assert.assertEquals("Александра", driver.findElement(By.id("person_firstName")).
                getAttribute("value"));
        Assert.assertEquals("Александровна", driver.findElement(By.id("person_middleName")).
                getAttribute("value"));
        Assert.assertEquals("08.12.1990", driver.findElement(By.id("person_birthDate")).
                getAttribute("value"));
        Assert.assertEquals("Женский", driver.findElement(
                By.xpath("//div[5]//label[contains(@class, 'active')]")).getText());


        Assert.assertEquals("Ivanov", driver.findElement(
                By.id("surname_vzr_ins_0")).getAttribute("value"));
        Assert.assertEquals("Ivan", driver.findElement(
                By.id("name_vzr_ins_0")).getAttribute("value"));
        Assert.assertEquals("02.02.1992", driver.findElement(
                By.id("birthDate_vzr_ins_0")).getAttribute("value"));

        // 11) Нажать продолжить
        webElement = driver.findElement(By.xpath("//button[contains(text(),'Продолжить')]"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", webElement);
        webElement.click();

        // 12) Проверить, что появилось сообщение - "При заполнении данных произошла ошибка", "Поле не заполнено"
        Thread.sleep(5000);
        Assert.assertEquals("Поле не заполнено.", driver.findElement(
                By.xpath("//input-phone2/span/validation-message/span")).getText());
        Assert.assertEquals("Поле не заполнено.", driver.findElement(
                By.xpath("//div[2]//input-email/span/validation-message/span")).getText());
        Assert.assertEquals("Поле не заполнено.", driver.findElement(
                By.xpath("//div[3]//input-email/span/validation-message/span")).getText());
        Assert.assertEquals("При заполнении данных произошла ошибка", driver.findElement(
                By.xpath("//*[contains(@role,'alert-form')]")).getText());
    }

    public void fillField(By locator, String value){
        driver.findElement(locator).clear();
        driver.findElement(locator).sendKeys(value);
    }

    @After
    public void afterTest(){
        driver.quit();
    }
}
