package org.sodfs.storage.driver.manager.local.controler;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReturnRequest implements Serializable {
    private int storageId;
    private boolean activeOrMoving;
    private int minNOR;

    ReturnRequest(int storageId, boolean activeOrMoving, int minNOR) {
        this.storageId = storageId;
        this.activeOrMoving = activeOrMoving;
        this.minNOR = minNOR;
    }

    public int getStorageId() {
        return storageId;
    }

    public boolean isActiveOrMoving() {
        return activeOrMoving;
    }

    public int getMinNOR() {
        return minNOR;
    } 
    
}
