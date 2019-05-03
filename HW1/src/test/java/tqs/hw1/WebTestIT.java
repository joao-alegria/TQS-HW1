/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1;

import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SeleniumExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class WebTestIT {

    WebDriver driver;

    @BeforeEach
    public void setup() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
    }

    @Test
    public void testT1() throws Exception {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("latitude")).click();
        driver.findElement(By.id("latitude")).clear();
        driver.findElement(By.id("latitude")).sendKeys("40");
        driver.findElement(By.id("longitude")).click();
        driver.findElement(By.id("longitude")).clear();
        driver.findElement(By.id("longitude")).sendKeys("-8");
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Longitude:'])[1]/following::input[2]")).click();
        assertEquals("Current Weather for latitude=40.00 and longitude=-8.00", driver.findElement(By.id("infoTitle")).getText());
        assertTrue(driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Wind Speed'])[1]/following::td[1]")).isDisplayed());
    }

    @Test
    public void testT2() throws Exception {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("multipleDaysTime")).click();
        driver.findElement(By.id("numDays")).click();
        driver.findElement(By.id("numDays")).clear();
        driver.findElement(By.id("numDays")).sendKeys("3");
        driver.findElement(By.id("numDays")).sendKeys(Keys.ENTER);
        driver.findElement(By.id("map-tab")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Longitude:'])[1]/following::canvas[1]")).click();
        assertEquals("Predictions for 3 days for latitude=43.91 and longitude=-8.08", driver.findElement(By.id("infoTitle")).getText());
        assertTrue(driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='undefined'])[2]/following::td[4]")).isDisplayed());
    }

    @Test
    public void testT3() throws Exception {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("multipleDaysTime")).click();
        driver.findElement(By.id("latitude")).click();
        driver.findElement(By.id("latitude")).clear();
        driver.findElement(By.id("latitude")).sendKeys("5");
        driver.findElement(By.id("longitude")).click();
        driver.findElement(By.id("longitude")).clear();
        driver.findElement(By.id("longitude")).sendKeys("5");
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Longitude:'])[1]/following::input[2]")).click();
        assertEquals("Predictions for the entire week for latitude=5.00 and longitude=5.00", driver.findElement(By.id("infoTitle")).getText());
        assertTrue(driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='rain'])[7]/following::td[4]")).isDisplayed());
    }

    @AfterEach
    public void destroy() {
        if (driver != null) {
            driver.close();
        }
    }

}
