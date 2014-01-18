package org.sodfs.storage.driver.manager.exceptions;

/**
 *
 * @author Roman Kierzkowski
 */
public class InconsistentReplicaException extends MovableFileException {

    public InconsistentReplicaException(int storageId, int fileId) {
        super("File " + fileId + " replica is might be inconsistent on the storage server " + storageId + ".");
    }
    
}
