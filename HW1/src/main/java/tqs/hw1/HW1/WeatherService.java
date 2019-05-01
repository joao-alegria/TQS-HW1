/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.HW1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author joaoalegria
 */
@Service
public class WeatherService {
    
    private MyCache mc;
    
    @Autowired
    private DarkSkyService dss;
    
    public WeatherService(){
        this.mc =new MyCache.Builder<List, List>().TTL(0.15*60*1000).updateTime(0.15*60*1000).build();
    }
    
    public List getWeather(double latitude, double longitude){
        List key = new ArrayList(Arrays.asList(latitude, longitude));
        List predictions;
        if(this.mc.containsKey(key)){
            try {
                predictions = (List)this.mc.get(key);
            } catch (Exception ex) {
                Logger.getLogger(WeatherService.class.getName()).log(Level.SEVERE, null, ex);
                predictions = dss.getPredictions(latitude, longitude);
                this.mc.put(key, predictions);
            }
        }else{
            predictions = dss.getPredictions(latitude, longitude);
            this.mc.put(key, predictions);
        }
        return predictions;
    }
    
    public MyCache getCache(){
        return this.mc;
    }

    public List getWeatherLimited(Double latitude, Double longitude, Integer numberPred) {
        List key = new ArrayList(Arrays.asList(latitude, longitude));
        List predictions;
        if(this.mc.containsKey(key)){
            try {
                predictions = (List)this.mc.getLimited(key, numberPred);
            } catch (Exception ex) {
                Logger.getLogger(WeatherService.class.getName()).log(Level.SEVERE, null, ex);
                predictions = dss.getPredictions(latitude, longitude).subList(0, numberPred);
                this.mc.put(key, predictions);
            }
        }else{
            predictions = dss.getPredictions(latitude, longitude).subList(0, numberPred);;
            this.mc.put(key, predictions);
        }
        return predictions;
    }

    public Map getCurrentWeather(Double latitude, Double longitude) {
        List key = new ArrayList(Arrays.asList(latitude, longitude));
        List predictions;
        if(this.mc.containsKey(key)){
            try {
                predictions = (List)this.mc.get(key);
            } catch (Exception ex) {
                Logger.getLogger(WeatherService.class.getName()).log(Level.SEVERE, null, ex);
                predictions = dss.getPredictions(latitude, longitude);
                this.mc.put(key, predictions);
            }
        }else{
            predictions = dss.getPredictions(latitude, longitude);
            this.mc.put(key, predictions);
        }
        Map current = (Map)predictions.get(0);
        return current;
    }

    public Map getCacheMetrics() {
        return this.mc.getMetrics();
    }
    
}
