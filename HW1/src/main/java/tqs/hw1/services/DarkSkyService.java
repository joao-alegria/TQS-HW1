package tqs.hw1.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Service used as a representation of the external api used to obtains all the predictions information.
 * Uses the SpringBoot component Service to help in its creation.
 */
@Service
public class DarkSkyService {
    
    private final Client client = ClientBuilder.newClient();
    private static final String URL = "https://api.darksky.net/forecast/";
    private static final String TOKEN = "62941f23eb66f081a83f3c443e509bf3";
    
    /**
     * Makes a request to the external api and processes it to obtain only the prediction of the
     * upcoming week.
     * @param latitude the latitude of the location to be processed.
     * @param longitude the longitude of the location to be processed.
     * @return a list with all the predictions.
     */
    public List getPredictions(Double latitude, Double longitude){
        JSONObject data = client
                            .target(URL+TOKEN+"/"+latitude+","+longitude)
                            .request(MediaType.APPLICATION_JSON)
                            .get(JSONObject.class);
        List dayPred = (List)((Map)data.get("daily")).get("data");
        
        
        String[] keys = new String[] {"time", "humidity", "ozone", "precipProbability", "precipType", "pressure","visibility","windSpeed"};
        
        
        List output = new ArrayList();
        for(Object pred : dayPred){
            Map p = (Map)pred;
            Map myPred = new HashMap();
            for(Object entry : p.entrySet()){
                Map.Entry e = (Map.Entry)entry;
                if(Arrays.asList(keys).contains(e.getKey())){
                    myPred.put(e.getKey(), e.getValue());
                }
            }
            output.add(myPred);
        }
        
        output.sort((Object t, Object t1) -> {
            Map m1 = (Map) t;
            Map m2 = (Map) t1;
            return ((int)m1.get("time"))-((int)m2.get("time"));            
        });
        return output;
    }
    
}
