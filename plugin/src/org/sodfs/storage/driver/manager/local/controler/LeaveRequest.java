package org.sodfs.storage.driver.manager.local.controler;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public class LeaveRequest implements Serializable {
    private int storageId;

    public LeaveRequest(int storageId) {
        this.storageId = storageId;
    }
    
    public int getStorageId() {
        return storageId;
    }
}
