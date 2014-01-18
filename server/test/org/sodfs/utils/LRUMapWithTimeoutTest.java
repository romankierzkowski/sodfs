package org.sodfs.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Roman Kierzkowski
 */
public class LRUMapWithTimeoutTest {

    public LRUMapWithTimeoutTest() {
    }

    /**
     * Test of put method, of class LRUMapWithTimeout.
     */
    @Test
    public void put() throws InterruptedException {
        LRUMapWithTimeout<Integer, Integer> instance = new LRUMapWithTimeout<Integer, Integer>(2);
        instance.put(1, 1, Clock.SECOND);
        assertNotNull(instance.get(1));
        assertNotNull(instance.get(1));
        Thread.sleep(Clock.SECOND);
        assertNull(instance.get(1));
        
        instance.put(1, 1, Clock.SECOND);
        instance.put(2, 2, Clock.SECOND);
        assertNotNull(instance.get(1));
        assertNotNull(instance.get(2));
        instance.put(3, 3, Clock.SECOND);
        assertNull(instance.get(1));
        assertNotNull(instance.get(2));
        assertNotNull(instance.get(3));
    }

}