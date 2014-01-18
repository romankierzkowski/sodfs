package org.sodfs.utils;

import java.util.Collections;
import java.util.Map;
import org.apache.commons.collections.map.LRUMap;

/**
 *
 * @author Roman Kierzkowski
 */
public class LRUMapWithTimeout<K,T> {
    private Map map;
    private Clock clock = Clock.getInstance();
    
    public LRUMapWithTimeout(int maxSize) {
        map = Collections.synchronizedMap(new LRUMap(maxSize));
    }
    
    public void put(K key, T object, long ttl) {
        map.put(key, new Record(object, ttl));        
    }
    
    public T get(K key) {
        Record r = (Record) map.get(key);
        T value = null;
        if (r != null && r.getTimeOut() > clock.getCurrentTime()) {
            value = r.getValue();          
        } else {
            map.remove(key);  
        }     
        return value;
    }

    public void remove(K key) {
        map.remove(key);
    }
    
    private class Record {
        private T value;
        private long timeOut;

        public Record(T object, long ttl) {
            this.value = object;
            this.timeOut = clock.getCurrentTime() + ttl;
        }

        public T getValue() {
            return value;
        }

        public long getTimeOut() {
            return timeOut;
        }
    }      
}
