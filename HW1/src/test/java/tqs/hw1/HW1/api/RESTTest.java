/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.HW1.api;

import tqs.hw1.api.REST;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import tqs.hw1.services.WeatherService;

/**
 *
 * @author joaoalegria
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(value=REST.class)
public class RESTTest {
    
    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private WeatherService weatherService;
    
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
        Mockito.when(weatherService.getCurrentWeather(0.0, 0.0)).thenReturn(obj);
        Mockito.when(weatherService.getWeather(0.0, 0.0)).thenReturn(retVal);
        Mockito.when(weatherService.getWeatherLimited(0.0, 0.0, 2)).thenReturn(retVal.subList(0, 2));
        Mockito.when(weatherService.getCacheMetrics()).thenReturn(obj);
    }

    /**
     * Test of weather method, of class REST.
     */
    @Test
    public void testWeather() throws Exception {
        System.out.println("weather");
        Double latitude = 0.0;
        Double longitude = 0.0;
        mvc.perform(get("/api/weather/"+latitude.toString()+","+longitude.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", hasKey("key1")))
                .andExpect(jsonPath("$[0]", hasKey("key2")))
                .andExpect(jsonPath("$[0]", hasKey("key3")));
    }

    /**
     * Test of currentWeather method, of class REST.
     */
    @Test
    public void testCurrentWeather() throws Exception {
        System.out.println("currentWeather");
        Double latitude = 0.0;
        Double longitude = 0.0;
        mvc.perform(get("/api/weather/"+latitude.toString()+","+longitude.toString()+"/now")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("key1")))
                .andExpect(jsonPath("$", hasKey("key2")))
                .andExpect(jsonPath("$", hasKey("key3")));
    }

    /**
     * Test of weatherLimited method, of class REST.
     */
    @Test
    public void testWeatherLimited() throws Exception {
        System.out.println("weatherLimited");
        Double latitude = 0.0;
        Double longitude = 0.0;
        Integer number = 2;
        mvc.perform(get("/api/weather/"+latitude.toString()+","+longitude.toString()+"/"+number.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", hasKey("key1")))
                .andExpect(jsonPath("$[0]", hasKey("key2")))
                .andExpect(jsonPath("$[0]", hasKey("key3")));
    }

    /**
     * Test of cacheStatus method, of class REST.
     */
    @Test
    public void testCacheStatus() throws Exception {
        System.out.println("cacheStatus");
        mvc.perform(get("/api/cacheStatus")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("key1")))
                .andExpect(jsonPath("$", hasKey("key2")))
                .andExpect(jsonPath("$", hasKey("key3")));
    }
    
}
