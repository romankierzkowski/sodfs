package org.sodfs.storage.driver.manager.exceptions;

/**
 *
 * @author Roman Kierzkowski 
 */
public class NotExistingReplicaException extends MovableFileException {

    public NotExistingReplicaException(int storageId, int fileId) {
        super("File " + fileId + " replica is not stored on the storage server " + storageId + ".");
    }
    
}
