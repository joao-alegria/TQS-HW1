package tqs.hw1.api;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tqs.hw1.services.WeatherService;


/**
 * Component responsible for mapping all the requests to the internal service.
 * Uses the SpringBoot component RestController to help this mapping.
 */
@RestController
@RequestMapping("api")
public class REST {
    
    @Autowired
    WeatherService ws;
    
    /**
     * Endpoint used to get the prediction of the entire next week of a specific location.
     * Example: X.X.X.X:Y/api/weather/50,50
     * @param latitude the latitude of the location to be processed.
     * @param longitude the longitude of the location to be processed.
     * @return a list of the different predictions.
     */
    @GetMapping(value="/weather/{latitude},{longitude}")
    public List weather(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude) {
        return ws.getWeather(latitude, longitude);
    }
    
    /**
     * Endpoint used to get the current prediction.
     * Example: X.X.X.X:Y/api/weather/50,50/now
     * @param latitude the latitude of the location to be processed.
     * @param longitude the longitude of the location to be processed.
     * @return a map of the current prediction.
     */
    @GetMapping(value="/weather/{latitude},{longitude}/now")
    public Map currentWeather(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude) {
        return ws.getCurrentWeather(latitude, longitude);
    }
    
    /**
     * Endpoint used to get the prediction of the next week, limited by the number indicated.
     * Example: X.X.X.X:Y/api/weather/50,50/5
     * @param latitude the latitude of the location to be processed.
     * @param longitude the longitude of the location to be processed.
     * @param numberPred the number of days to be predicted, with the max of 7.
     * @return a list of the different predictions.
     */
    @GetMapping(value="/weather/{latitude},{longitude}/{numberPred}")
    public List weatherLimited(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude, @PathVariable("numberPred") Integer numberPred) {
        return ws.getWeatherLimited(latitude, longitude, numberPred);
    }
    
    /**
     * Endpoint used as debug, that helps understand how the internal cache is working. 
     * @return the string that represents the cache.
     */
    @GetMapping(value="/cache")
    public String teste() {
        return ws.getCache().toString();
    }
    
    /**
     * Endpoint used as debug, that helps understand how the internal cache is working. 
     * @return the map of the cache metrics.
     */
    @GetMapping(value="/cacheStatus")
    public Map cacheStatus() {
        return ws.getCacheMetrics();
    }
}
