package tqs.hw1.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Map like data structure that provides temporary storage.
 */
public class MyCache<T, K> {
    
    private Map<T, CacheObject> cache = new HashMap();
    private double defaultTtl;
    private double defaultUpdateTime;
    private int requests = 0;
    private int hits = 0;
    private int misses = 0;
    private Thread t;
    private boolean running=true;
    private String errorMessage1="The value of the key %s already expired.";
    private String errorMessage2="The key %s doesn't exist or expired.";

    
    /**
     * Cache object wrapper used to add additional information to the object inserted.
     * In this case the time it was inserted.
     */
    protected class CacheObject {
        private K value;
        private long lastAccessed;

        public CacheObject(K value) {
            this.value = value;
            this.lastAccessed = System.currentTimeMillis();
        } 

        @Override
        public String toString() {
            return "CacheObject{" + "value=" + value.toString() + ", lastAccessed=" + lastAccessed + '}';
        }    
    }

    /**
     * Auxiliary class, used for creating a builder to the cache.
     * @param <T> the object type of the key
     * @param <K> the object type of the value
     */
    public static class Builder<T,K> {

        private double ttl = 5.0 * 60.0 * 1000.0;
        private double updateTime = 5.0 * 60.0 * 1000.0;

        /**
         * Time to leave setter.
         * @param ttl double that indicates the time to leave of the values in milliseconds.
         * @return the Builder Object itself
         */
        public Builder ttl(double ttl) {
            this.ttl = ttl;
            return this;
        }

        /**
         * Update time of the worker thread setter.
         * @param updateTime double that indicates the time interval the values should be checked in milliseconds.
         * @return the Builder Object itself
         */
        public Builder updateTime(double updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        /**
         * Finalizes the building phase, inherit to the builder pattern.
         * @return the cache already built
         */
        public MyCache build() {
            MyCache<T,K> mc = new MyCache();
            mc.defaultTtl = this.ttl;
            mc.defaultUpdateTime = this.updateTime;
            return mc;
        }

    }

    /**
     * Constructor of the cache object, private because the builder pattern is used.
     */
    private MyCache() {
        t = new Thread(() -> {
            try {
                while (this.running) {
                    Thread.sleep((long) defaultUpdateTime);
                    cleanCache();
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                relaunchThread();
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    /**
     * Stops the worker thread of the cache and cleans every value that may exist.
     */
    public void stop(){
        this.running=false;
        try {
            for(T key : this.cache.keySet()){
                this.clear(key);
            }
        } catch (CacheException ex) {
            Logger.getLogger(MyCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Private method used when a error occurs during the execution of the worker thread.
     */
    private void relaunchThread(){
        t.setDaemon(true);
        t.start();
    }

    /**
     * If the value stored is of the type Iterable, the returned Object is a 
     * object limited by the number indicated, else the whole value stored is returned.
     * @param key the key of the value that should be returned, limited or not.
     * @param numberOfPred the number of sub-objects the value should be limited, if Iterable
     * @return the value stored or a limitation of that value
     * @throws CacheException that indicates the error that occurred
     */
    public Object getLimited(T key, int numberOfPred) throws CacheException {
        this.requests+=1;
        if(this.cache.containsKey(key)){
            long now = System.currentTimeMillis();
            CacheObject value = this.cache.get(key);
            if (value == null || (now > (defaultTtl + value.lastAccessed))){
                this.misses+=1;
                throw new CacheException(String.format(errorMessage1, key.toString()));
            }
            this.hits+=1;
            K predictions=value.value;
            if(predictions instanceof Iterable){
                List output = new ArrayList();
                Iterator it = ((Iterable)predictions).iterator();
                for(int i=0; i<numberOfPred; i++){
                    if(it.hasNext()){
                        output.add(it.next());
                    }
                    else{break;}
                }
                return output;
            }
            return predictions;
        }else{
            this.misses+=1;
            throw new CacheException(String.format(errorMessage2, key.toString()));
        }
    }

    /**
     * Returns the value stored in a specific key as it was stored.
     * @param key the key of the value that should be returned
     * @return the value as it was stored in the cache
     * @throws CacheException that indicates the error that occurred
     */
    public K get(T key) throws CacheException {
        this.requests+=1;
        if(this.cache.containsKey(key)){
            long now = System.currentTimeMillis();
            CacheObject value = this.cache.get(key);
            if (value == null || (now > (defaultTtl + value.lastAccessed))){
                this.misses+=1;
                throw new CacheException(String.format(errorMessage1, key.toString()));
            }
            this.hits+=1;
            return value.value;
        }else{
            this.misses+=1;
            throw new CacheException(String.format(errorMessage2, key.toString()));
        }
    }

    /**
     * Allows the insertion of a key - value pair in the cache.
     * @param key the key object to be inserted.
     * @param data the value object to be inserted.
     */
    public void put(T key, K data) {
        this.cache.put(key, new CacheObject(data));
        
    }

    /**
     * Removes the key - value pair of the cache if the key existes.
     * @param key the key object that should be removed.
     * @throws CacheException that indicates the error that occurred
     */
    public void clear(T key) throws CacheException {
        if(this.cache.containsKey(key)){
            this.cache.remove(key);
        }else{
            throw new CacheException(String.format(errorMessage2, key.toString()));
        }
    }

    /**
     * Checks if a key object exists in the cache.
     * @param key the key object to be checked.
     * @return a boolean that represents if the key exists or not.
     */
    public boolean containsKey(T key) {
        for(T k : this.cache.keySet()) {
            if (k.equals(key)) {
                    return true;
            }
        }
        return false;
    }

    /**
     * Private method used by the working thread to check which key - value pairs have already expired his time 
     * to leave.
     */
    private void cleanCache() {
        long now = System.currentTimeMillis();
        ArrayList<T> deleteKey = null;
 
        synchronized (cache) {
            deleteKey = new ArrayList();
            T key = null;
            CacheObject c = null;
 
            for(Map.Entry<T,CacheObject> entry : cache.entrySet()){
                key = entry.getKey();
                c = entry.getValue();
 
                if(c != null && (now > (defaultTtl + c.lastAccessed))){
                    deleteKey.add(key);
                }
            }
        }
 
        for (T key : deleteKey) {
            synchronized (cache) {
                cache.remove(key);
            }
 
            Thread.yield();
        }
    }

    /**
     * Creates a String representation of the object.
     * @return the string created.
     */
    @Override
    public String toString() {
        return "MyCache{" + "cache=" + cache.toString() + ", defaultTTL=" + defaultTtl + ", defaultUpdateTime=" + defaultUpdateTime + '}';
    }
    
    /**
     * Informs of the metrics of the cache, related to hits, misses and requests made to it.
     * @return a map with "hits", "misses" and "requests" as the keys and the respective values.
     */
    public Map getMetrics(){
        Map output =  new HashMap();
        output.put("hits", this.hits);
        output.put("misses", this.misses);
        output.put("requests", this.requests);
        return output;
    }
    
    /**
     * Informs of the size of the cache.
     * @return the number of key-value pairs stored.
     */
    public int size(){
        return this.cache.size();
    }
    
}
