package tqs.hw1.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joaoalegria
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

    public static class Builder<T,K> {

        private double ttl = 5.0 * 60.0 * 1000.0;
        private double updateTime = 5.0 * 60.0 * 1000.0;

        public Builder ttl(double ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder updateTime(double updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public MyCache build() {
            MyCache<T,K> mc = new MyCache();
            mc.defaultTtl = this.ttl;
            mc.defaultUpdateTime = this.updateTime;
            return mc;
        }

    }

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
    
    private void relaunchThread(){
        t.setDaemon(true);
        t.start();
    }

    public Object getLimited(T key, int numberOfPred) throws CacheException {
        this.requests+=1;
        if(this.cache.containsKey(key)){
            long now = System.currentTimeMillis();
            CacheObject value = this.cache.get(key);
            if (value == null || (now > (defaultTtl + value.lastAccessed))){
                this.misses+=1;
                throw new CacheException("The value of the key "+key.toString()+" already expired.");
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
            throw new CacheException("The key "+key.toString()+" doesn't exist or expired..");
        }
    }

    public K get(T key) throws CacheException {
        this.requests+=1;
        if(this.cache.containsKey(key)){
            long now = System.currentTimeMillis();
            CacheObject value = this.cache.get(key);
            if (value == null || (now > (defaultTtl + value.lastAccessed))){
                this.misses+=1;
                throw new CacheException("The value of the key "+key.toString()+" already expired.");
            }
            this.hits+=1;
            return value.value;
        }else{
            this.misses+=1;
            throw new CacheException("The key "+key.toString()+" doesn't exist or expired.");
        }
    }

    public void put(T key, K data) {
        this.cache.put(key, new CacheObject(data));
        
    }

    public void clear(T key) throws CacheException {
        if(this.cache.containsKey(key)){
            this.cache.remove(key);
        }else{
            throw new CacheException("The key "+key.toString()+" doesn't exist or expired..");
        }
    }

    public boolean containsKey(T key) {
        for(T k : this.cache.keySet()) {
            if (k.equals(key)) {
                    return true;
            }
        }
        return false;
    }

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

    @Override
    public String toString() {
        return "MyCache{" + "cache=" + cache.toString() + ", defaultTTL=" + defaultTtl + ", defaultUpdateTime=" + defaultUpdateTime + '}';
    }
    
    public Map getMetrics(){
        Map output =  new HashMap();
        output.put("hits", this.hits);
        output.put("misses", this.misses);
        output.put("requests", this.requests);
        return output;
    }
    
    public int size(){
        return this.cache.size();
    }
    
}
