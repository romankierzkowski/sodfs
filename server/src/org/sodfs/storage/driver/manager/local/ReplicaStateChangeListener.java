package org.sodfs.storage.driver.manager.local;

/**
 *
 * @author Roman Kierzkowski
 */
public interface ReplicaStateChangeListener {

    public void replicaStateChanged(ReplicaState state, int destinationStorageId);

}
