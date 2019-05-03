/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.api;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tqs.hw1.services.WeatherService;


/**
 *
 * @author joaoalegria
 */
@RestController
@RequestMapping("api")
public class REST {
    
    @Autowired
    WeatherService ws;
    
    @GetMapping(value="/weather/{latitude},{longitude}")
    public List weather(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude) {
        return ws.getWeather(latitude, longitude);
    }
    
    @GetMapping(value="/weather/{latitude},{longitude}/now")
    @CrossOrigin
    public Map currentWeather(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude) {
        return ws.getCurrentWeather(latitude, longitude);
    }
    
    @GetMapping(value="/weather/{latitude},{longitude}/{numberPred}")
    public List weatherLimited(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude, @PathVariable("numberPred") Integer numberPred) {
        return ws.getWeatherLimited(latitude, longitude, numberPred);
    }
    
    @GetMapping(value="/teste")
    public String teste() {
        return ws.getCache().toString();
    }
    
    @GetMapping(value="/cacheStatus")
    public Map cacheStatus() {
        return ws.getCacheMetrics();
    }
}
