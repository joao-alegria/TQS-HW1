/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.HW1;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author joaoalegria
 */
@RestController
@RequestMapping("api")
public class REST {
    
    @Autowired
    WeatherService ws;
    
    @RequestMapping(value="/weather/{latitude},{longitude}", method=GET)
    public List weather(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude) {
        return ws.getWeather(latitude, longitude);
    }
    
    @RequestMapping(value="/weather/{latitude},{longitude}/now", method=GET)
    public Map currentWeather(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude) {
        return ws.getCurrentWeather(latitude, longitude);
    }
    
    @RequestMapping(value="/weather/{latitude},{longitude}/{numberPred}", method=GET)
    public List weatherLimited(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude, @PathVariable("numberPred") Integer numberPred) {
        return ws.getWeatherLimited(latitude, longitude, numberPred);
    }
    
    @RequestMapping(value="/teste", method=GET)
    public String teste() {
        return ws.getCache().toString();
    }
    
    @RequestMapping(value="/cacheStatus", method=GET)
    public Map cacheStatus() {
        return ws.getCacheMetrics();
    }
}
