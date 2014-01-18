package org.sodfs.storage.driver.manager.local.controler;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public class JoinOrder implements Serializable {
    private int storageId;

    public JoinOrder(int storageId) {
        this.storageId = storageId;
    }
    
    public int getStorageId() {
        return storageId;
    }
}
