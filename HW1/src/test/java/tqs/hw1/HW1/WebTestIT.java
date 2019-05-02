/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.HW1;

import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.context.WebApplicationContext;


@ExtendWith(SeleniumExtension.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class WebTestIT {

    @Autowired
    WebApplicationContext context;

    @LocalServerPort
    int port;
    
    WebDriver driver;

    @BeforeEach
    public void setup() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
    }
    
    @Test
    public void testTQSHW1() throws Exception {
        System.out.println("aqui");
        driver.get("http://localhost:"+port+"/");
        driver.findElement(By.id("latitude")).click();
        driver.findElement(By.id("latitude")).clear();
        driver.findElement(By.id("latitude")).sendKeys("40");
        driver.findElement(By.id("longitude")).click();
        driver.findElement(By.id("longitude")).clear();
        driver.findElement(By.id("longitude")).sendKeys("-8");
        driver.findElement(By.id("myTabContent")).click();
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Longitude:'])[1]/following::input[2]")).click();
        driver.findElement(By.id("showInfo")).click();
        assertEquals("Current Weather", driver.findElement(By.id("infoTitle")).getText());
    }

    
    @AfterEach
    public void destroy() {
        if (driver != null) {
            driver.close();
        }
    }
}
