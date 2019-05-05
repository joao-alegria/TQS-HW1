/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
    
    
    private List expList;
    private Map expMap;
    
    @BeforeEach
    public void setUp() {
        expList =  new ArrayList();
        expMap = new HashMap();
        expMap.put("key1", "value1");
        expMap.put("key2", "value2");
        expMap.put("key3", "value3");
        expList.add(expMap);
        expList.add(expMap);
        expList.add(expMap);
        Mockito.when(darkService.getPredictions(0.0, 0.0)).thenReturn(expList);
    }

    /**
     * Test of getWeather method, of class WeatherService.
     */
    @Test
    public void testGetWeather() {
        System.out.println("getWeather");
        double latitude = 0.0;
        double longitude = 0.0;
        List result = weatherService.getWeather(latitude, longitude);
        assertEquals(expList, result);
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
        List result = weatherService.getWeatherLimited(latitude, longitude, numberPred);
        assertEquals(expList.subList(0, numberPred), result);
    }

    /**
     * Test of getCurrentWeather method, of class WeatherService.
     */
    @Test
    public void testGetCurrentWeather() {
        System.out.println("getCurrentWeather");
        double latitude = 0.0;
        double longitude = 0.0;
        Map result = weatherService.getCurrentWeather(latitude, longitude);
        assertEquals(expMap, result);
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
    
    /**
     * Test of repeating get so that the cache is used.
     */
    @Test
    public void testRepeatGet() {
        System.out.println("repeatGet");
        double latitude = 0.0;
        double longitude = 0.0;
        assertEquals(0, weatherService.getCacheMetrics().get("requests"));
        assertEquals(0, weatherService.getCacheMetrics().get("hits"));
        weatherService.getWeather(latitude, longitude);
        assertEquals(0, weatherService.getCacheMetrics().get("requests"));
        assertEquals(0, weatherService.getCacheMetrics().get("hits"));
        weatherService.getWeather(latitude, longitude);
        assertEquals(1, weatherService.getCacheMetrics().get("requests"));
        assertEquals(1, weatherService.getCacheMetrics().get("hits"));
    }
    
    /**
     * Test of repeating get so that the cache is used, but waiting so that the ttl has passed.
     */
    @Test
    public void testRepeatGetWait() throws InterruptedException {
        System.out.println("repeatGetWait");
        double latitude = 0.0;
        double longitude = 0.0;
        weatherService.resetCache(5.0*1000.0, 20.0*1000.0);
        assertEquals(0, weatherService.getCacheMetrics().get("requests"));
        assertEquals(0, weatherService.getCacheMetrics().get("hits"));
        assertEquals(0, weatherService.getCacheMetrics().get("misses"));
        weatherService.getWeather(latitude, longitude);
        assertEquals(0, weatherService.getCacheMetrics().get("requests"));
        assertEquals(0, weatherService.getCacheMetrics().get("hits"));
        assertEquals(0, weatherService.getCacheMetrics().get("misses"));
        weatherService.getWeather(latitude, longitude);
        assertEquals(1, weatherService.getCacheMetrics().get("requests"));
        assertEquals(1, weatherService.getCacheMetrics().get("hits"));
        assertEquals(0, weatherService.getCacheMetrics().get("misses"));
        TimeUnit.SECONDS.sleep(6);
        weatherService.getWeather(latitude, longitude);
        assertEquals(2, weatherService.getCacheMetrics().get("requests"));
        assertEquals(1, weatherService.getCacheMetrics().get("hits"));
        assertEquals(1, weatherService.getCacheMetrics().get("misses"));
    }
    
}
