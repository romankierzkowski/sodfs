package org.sodfs.utils;

import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Roman Kierzkowski
 */
public class LRUMap<K,T> {
    private Map map;
    
    public LRUMap(int maxSize) {
        map = Collections.synchronizedMap(new org.apache.commons.collections.map.LRUMap(maxSize));
    }
    
    public void put(K key, T object) {
        map.put(key, object);        
    }
    
    public T get(K key) { 
        return (T) map.get(key);
    } 
}