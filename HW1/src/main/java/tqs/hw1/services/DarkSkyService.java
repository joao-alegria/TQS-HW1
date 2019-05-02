/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.services;

import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

/**
 *
 * @author joaoalegria
 */
@Service
public class DarkSkyService {
    
    private final Client client = ClientBuilder.newClient();
    private final static String url = "https://api.darksky.net/forecast/";
    private final static String token = "62941f23eb66f081a83f3c443e509bf3";
    
    public List getPredictions(Double latitude, Double longitude){
        JSONObject data = client
                            .target(url+token+"/"+latitude+","+longitude)
                            .request(MediaType.APPLICATION_JSON)
                            .get(JSONObject.class);
        List dayPred = (List)((Map)data.get("daily")).get("data");
        dayPred.sort((Object t, Object t1) -> {
            Map m1 = (Map) t;
            Map m2 = (Map) t1;
            return ((int)m1.get("time"))-((int)m2.get("time"));            
        });
        return dayPred;
    }
    
}
