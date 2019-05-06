package tqs.hw1.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.hw1.persistence.MyCache;

/**
 * Main service that responds to the requests made.
 * Uses the SpringBoot component Service to help in its creation.
 */
@Service
public class WeatherService {
    
    private MyCache mc;
    
    @Autowired
    private DarkSkyService dss;
    
    /**
     * Resets the internal cache, given the possibility to update the time to live and update times inserted.
     * @param ttl a double of the new time to leave
     * @param update a double of the new update time
     */
    public void resetCache(double ttl, double update){
        this.mc.stop();
        this.mc =new MyCache.Builder<List, List>().ttl(ttl).updateTime(update).build();
    }
    
    /**
     * Class creator, that internally initializes the cache.
     */
    public WeatherService(){
        Double ttl = 1 * 60.0 * 1000.0;
        Double update = 0.01 * 60.0 * 1000.0;
        this.mc =new MyCache.Builder<List, List>().ttl(ttl).updateTime(update).build();
    }
    
    /**
     * Obtains the predictions of the entire next week.
     * @param latitude the latitude of the location to be processed.
     * @param longitude the longitude of the location to be processed.
     * @return a list of the different predictions.
     */
    public List getWeather(double latitude, double longitude){
        return this.getExternalPredictions(latitude, longitude, -1);
    }
    
    /**
     * Debug method.
     * @return the internal cache.
     */
    public MyCache getCache(){
        return this.mc;
    }

    /**
     * Obtains the predictions of the next days, according to the number of days indicated.
     * @param latitude the latitude of the location to be processed.
     * @param longitude the longitude of the location to be processed.
     * @param numberPred the number of days to be predicted, with the max of 7.
     * @return a list of the different predictions.
     */
    public List getWeatherLimited(Double latitude, Double longitude, Integer numberPred) {
        if(8<numberPred || numberPred<0){
            return Collections.emptyList();
        }
        return this.getExternalPredictions(latitude, longitude, numberPred);
    }

    /**
     * Obtains the current prediction of the indicated location.
     * @param latitude the latitude of the location to be processed.
     * @param longitude the longitude of the location to be processed.
     * @return a map of the current prediction.
     */
    public Map getCurrentWeather(Double latitude, Double longitude) {
        List predictions = this.getExternalPredictions(latitude, longitude, -1);
        return (Map)predictions.get(0);
    }

    /**
     * Debug method that is used to obtain the internal cache metrics.
     * @return a map with all the metrics of the internal cache.
     */
    public Map getCacheMetrics() {
        return this.mc.getMetrics();
    }
    
    /**
     * Private method used to make the request to the external api to get the predictions information.
     * @param latitude the latitude of the location to be processed.
     * @param longitude the longitude of the location to be processed.
     * @param number the number of days to be predicted, with the max of 7.
     * @return a list of the predictions
     */
    private List getExternalPredictions(Double latitude, Double longitude, int number){
        List key = new ArrayList(Arrays.asList(latitude, longitude));
        List predictions;
        if(this.mc.containsKey(key)){
            try {
                if(number==-1){
                    predictions = (List)this.mc.get(key);
                }else{
                    predictions = (List)this.mc.getLimited(key, number);
                }
            } catch (Exception ex) {
                Logger.getLogger(WeatherService.class.getName()).log(Level.SEVERE, null, ex);
                if(number==-1){
                    predictions = dss.getPredictions(latitude, longitude);
                }else{
                    predictions = dss.getPredictions(latitude, longitude).subList(0, number);
                }
                this.mc.put(key, predictions);
            }
        }else{
            if(number==-1){
                predictions = dss.getPredictions(latitude, longitude);
            }else{
                predictions = dss.getPredictions(latitude, longitude).subList(0, number);
            }
            this.mc.put(key, predictions);
        }
        return predictions;
    }

}
