/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.hw1.HW1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 *
 * @author joaoalegria
 */
public class MyCacheTest {
    
    /**
     * Test of if it throws exception if the key isn't there.
     */
    @Test
    public void testNoKey() throws Exception {
        System.out.println("noKey");
        String key="key";
        MyCache instance =new MyCache.Builder<String, String>().TTL(0.5*60*1000).updateTime(0.5*60*1000).build();
        assertThrows(Exception.class, () -> {instance.get(key);});
        assertThrows(Exception.class, () -> {instance.getLimited(key,2);});
        assertThrows(Exception.class, () -> {instance.clear(key);});
    }

    /**
     * Test of getLimited method, of class MyCache.
     */
    @Test
    public void testGetLimited() throws Exception {
        System.out.println("getLimited");
        Object key = "key";
        Object data = "value";
        int numberPred = 0;
        MyCache instance =new MyCache.Builder<String, String>().TTL(0.5*60*1000).updateTime(0.5*60*1000).build();
        assertEquals(false, instance.containsKey(key));
        instance.put(key, data);
        assertEquals(true, instance.containsKey(key));
        assertEquals(data, instance.getLimited(key, numberPred));
    }

    /**
     * Test when the value is iterable, if it returns limited elements.
     */
    @Test
    public void testGetLimitedArray() throws Exception {
        System.out.println("getLimited with list value");
        Object key = "key";
        Object data = new ArrayList(Arrays.asList("t1", "t2", "t3"));
        MyCache instance =new MyCache.Builder<String, List>().TTL(0.5*60*1000).updateTime(0.5*60*1000).build();
        assertEquals(false, instance.containsKey(key));
        instance.put(key, data);
        assertEquals(true, instance.containsKey(key));
        int numberPred = 0;
        assertEquals(Collections.EMPTY_LIST, instance.getLimited(key, numberPred));
        numberPred = 2;
        List result = (List) instance.getLimited(key, numberPred);
        assertEquals(2, result.size());
    }
    
    /**
     * Test of get method, of class MyCache.
     */
    @Test
    public void testGet() throws Exception {
        System.out.println("get");
        Object key = "key";
        Object data = "value";
        MyCache instance =new MyCache.Builder<String, String>().TTL(0.5*60*1000).updateTime(0.5*60*1000).build();
        assertEquals(false, instance.containsKey(key));
        instance.put(key, data);
        assertEquals(true, instance.containsKey(key));
        assertEquals(data, instance.get(key));
    }

    /**
     * Test of put method, of class MyCache.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        Object key = "key";
        Object data = "value";
        MyCache instance =new MyCache.Builder<String, String>().TTL(0.5*60*1000).updateTime(0.5*60*1000).build();
        assertEquals(0, instance.size());
        assertEquals(false, instance.containsKey(key));
        instance.put(key, data);
        assertEquals(true, instance.containsKey(key));
        assertEquals(1, instance.size());
        
    }

    /**
     * Test of clear method, of class MyCache.
     */
    @Test
    public void testClear() throws Exception{
        System.out.println("clear");
        Object key = null;
        MyCache instance =new MyCache.Builder<String, String>().TTL(0.5*60*1000).updateTime(0.5*60*1000).build();
        assertEquals(0, instance.size());
        instance.put(key, "value");
        assertEquals(1, instance.size());
        instance.clear(key);
        assertEquals(0, instance.size());
    }

    /**
     * Test of containsKey method, of class MyCache.
     */
    @Test
    public void testContainsKey() {
        System.out.println("containsKey");
        Object key = "key";
        MyCache instance =new MyCache.Builder<String, String>().TTL(0.5*60*1000).updateTime(0.5*60*1000).build();
        boolean expResult = false;
        boolean result = instance.containsKey(key);
        assertEquals(expResult, result);
        instance.put(key, "value");
        expResult=true;
        result = instance.containsKey(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMetrics method, of class MyCache.
     */
    @Test
    public void testGetMetrics() {
        System.out.println("getMetrics");
        MyCache instance =new MyCache.Builder<String, String>().TTL(0.5*60*1000).updateTime(0.5*60*1000).build();
        Map result = instance.getMetrics();
        assertEquals(true, result.containsKey("misses"));
        assertEquals(true, result.containsKey("hits"));
        assertEquals(true, result.containsKey("requests"));
    }
    
    /**
     * Test if the time out works.
     */
    @Test
    public void testTimeOut() throws InterruptedException {
        System.out.println("timeOut");
        MyCache instance =new MyCache.Builder<String, String>().TTL(0.1*60*1000).updateTime(0.01*60*1000).build();
        String key="key";
        String data="value";
        instance.put(key, data);
        assertEquals(true, instance.containsKey(key));
        assertEquals(1, instance.size());
        TimeUnit.SECONDS.sleep(7);
        assertEquals(false, instance.containsKey(key));
        assertEquals(0, instance.size());
    }
    
    /**
     * Test hits and misses.
     */
    @Test
    public void testHitsMisses() throws Exception {
        System.out.println("hitsMisses");
        MyCache instance =new MyCache.Builder<String, String>().TTL(0.1*60*1000).updateTime(0.01*60*1000).build();
        String key="key";
        String data="value";
        instance.put(key, data);
        instance.get(key);
        Map result = (Map) instance.getMetrics();
        assertEquals(1, result.get("requests"));
        assertEquals(0, result.get("misses"));
        assertEquals(1, result.get("hits"));
        TimeUnit.SECONDS.sleep(7);
        try {
            instance.get(key);
        } catch (Exception ex) {
            Logger.getLogger(MyCacheTest.class.getName()).log(Level.INFO, "Exception thrown as expected when testing hits and misses.");
            result = (Map) instance.getMetrics();
            assertEquals(2, result.get("requests"));
            assertEquals(1, result.get("misses"));
            assertEquals(1, result.get("hits"));
        }
    }
    
}
