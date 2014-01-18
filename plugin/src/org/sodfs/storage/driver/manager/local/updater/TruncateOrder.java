package org.sodfs.storage.driver.manager.local.updater;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public class TruncateOrder implements Serializable {
    private long size;

    public TruncateOrder(long size) {
        this.size = size;
    }
    
    public long getSize() {
        return size;
    }   
}
