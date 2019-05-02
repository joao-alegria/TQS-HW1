/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.services;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author joaoalegria
 */
public class DarkSkyServiceTest {
    
    public DarkSkyServiceTest() {
    }

    /**
     * Test of getPredictions method, of class DarkSkyService.
     */
    @Test
    public void testGetPredictions() {
        System.out.println("getPredictions");
        Double latitude = 40.0;
        Double longitude = -8.0;
        DarkSkyService instance = new DarkSkyService();
        List result = instance.getPredictions(latitude, longitude);
        Map obj =  (Map)result.get(0);
        assertTrue(obj.containsKey("time"));
        assertTrue(obj.containsKey("pressure"));
    }
    
}
