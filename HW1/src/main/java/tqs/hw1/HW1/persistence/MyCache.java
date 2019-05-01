package tqs.hw1.HW1.persistence;

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
    private double defaultTTL, defaultUpdateTime;
    private int requests = 0, hits = 0, misses = 0;

    protected class CacheObject {
        public K value;
        public long lastAccessed;

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

        private double TTL, updateTime = 5 * 60 * 1000;

        public Builder TTL(double TTL) {
            this.TTL = TTL;
            return this;
        }

        public Builder updateTime(double updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public MyCache build() {
            MyCache<T,K> mc = new MyCache();
            mc.defaultTTL = this.TTL;
            mc.defaultUpdateTime = this.updateTime;
            return mc;
        }

    }

    private MyCache() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep((long) defaultUpdateTime);
                    } catch (InterruptedException ex) {
                    }
                    cleanCache();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public Object getLimited(T key, int numberOfPred) throws Exception {
        this.requests+=1;
        if(this.cache.containsKey(key)){
            long now = System.currentTimeMillis();
            CacheObject value = this.cache.get(key);
            if (value == null || (now > (defaultTTL + value.lastAccessed))){
                this.misses+=1;
                throw new Exception("The value of the key "+key.toString()+" already expired.");
            }
            this.hits+=1;
            K predictions=(K)value.value;
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
            throw new Exception("The key "+key.toString()+" doesn't exist or expired..");
        }
    }

    public K get(T key) throws Exception {
        this.requests+=1;
        if(this.cache.containsKey(key)){
            long now = System.currentTimeMillis();
            CacheObject value = this.cache.get(key);
            if (value == null || (now > (defaultTTL + value.lastAccessed))){
                this.misses+=1;
                throw new Exception("The value of the key "+key.toString()+" already expired.");
            }
            this.hits+=1;
            K predictions=(K)value.value;
            return predictions;
        }else{
            this.misses+=1;
            throw new Exception("The key "+key.toString()+" doesn't exist or expired.");
        }
    }

    public void put(T key, K data) {
        this.cache.put(key, new CacheObject(data));
        
    }

    public void clear(T key) throws Exception {
        if(this.cache.containsKey(key)){
            this.cache.remove(key);
        }else{
            throw new Exception("The key "+key.toString()+" doesn't exist or expired..");
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
 
            for(T k : cache.keySet()){
                key = k;
                c = (CacheObject) cache.get(k);
 
                if(c != null && (now > (defaultTTL + c.lastAccessed))){
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
        return "MyCache{" + "cache=" + cache.toString() + ", defaultTTL=" + defaultTTL + ", defaultUpdateTime=" + defaultUpdateTime + '}';
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
