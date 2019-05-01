/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.HW1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author joaoalegria
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WeatherServiceTest {
    
    @TestConfiguration
    static class WeatherServiceTestContextConfiguration {
  
        @Bean
        public WeatherService weatherService() {
            return new WeatherService();
        }
    }
 
    @InjectMocks
    private WeatherService weatherService;
 
    @Mock
    private DarkSkyService darkService;
    
    @BeforeEach
    public void setUp() {
        List retVal =  new ArrayList();
        Map obj = new HashMap();
        obj.put("key1", "value1");
        obj.put("key2", "value2");
        obj.put("key3", "value3");
        retVal.add(obj);
        retVal.add(obj);
        retVal.add(obj);
        Mockito.when(darkService.getPredictions(0.0, 0.0)).thenReturn(retVal);
    }

    /**
     * Test of getWeather method, of class WeatherService.
     */
    @Test
    public void testGetWeather() {
        System.out.println("getWeather");
        double latitude = 0.0;
        double longitude = 0.0;
        List expResult =  new ArrayList();
        Map obj = new HashMap();
        obj.put("key1", "value1");
        obj.put("key2", "value2");
        obj.put("key3", "value3");
        expResult.add(obj);
        expResult.add(obj);
        expResult.add(obj);
        List result = weatherService.getWeather(latitude, longitude);
        assertEquals(expResult, result);
    }

    /**
     * Test of getWeatherLimited method, of class WeatherService.
     */
    @Test
    public void testGetWeatherLimited() {
        System.out.println("getWeatherLimited");
        double latitude = 0.0;
        double longitude = 0.0;
        Integer numberPred = 2;
        List expResult =  new ArrayList();
        Map obj = new HashMap();
        obj.put("key1", "value1");
        obj.put("key2", "value2");
        obj.put("key3", "value3");
        expResult.add(obj);
        expResult.add(obj);
        List result = weatherService.getWeatherLimited(latitude, longitude, numberPred);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentWeather method, of class WeatherService.
     */
    @Test
    public void testGetCurrentWeather() {
        System.out.println("getCurrentWeather");
        double latitude = 0.0;
        double longitude = 0.0;
        Map expResult = new HashMap();
        expResult.put("key1", "value1");
        expResult.put("key2", "value2");
        expResult.put("key3", "value3");
        Map result = weatherService.getCurrentWeather(latitude, longitude);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCacheMetrics method, of class WeatherService.
     */
    @Test
    public void testGetCacheMetrics() {
        System.out.println("getCacheMetrics");
        Map result = weatherService.getCacheMetrics();
        assertEquals(weatherService.getCache().getMetrics(), result);
    }
    
}
