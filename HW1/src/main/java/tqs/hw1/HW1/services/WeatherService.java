/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.HW1.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.hw1.HW1.persistence.MyCache;

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
        Double ttl = 1 * 60.0 * 1000.0;
        Double update = 0.01 * 60.0 * 1000.0;
        this.mc =new MyCache.Builder<List, List>().ttl(ttl).updateTime(update).build();
    }
    
    public List getWeather(double latitude, double longitude){
        List predictions = this.getExternalPredictions(latitude, longitude, -1);
        return predictions;
    }
    
    public MyCache getCache(){
        return this.mc;
    }

    public List getWeatherLimited(Double latitude, Double longitude, Integer numberPred) {
        List predictions = this.getExternalPredictions(latitude, longitude, numberPred);
        return predictions;
    }

    public Map getCurrentWeather(Double latitude, Double longitude) {
        List predictions = this.getExternalPredictions(latitude, longitude, -1);
        Map current = (Map)predictions.get(0);
        return current;
    }

    public Map getCacheMetrics() {
        return this.mc.getMetrics();
    }
    
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
