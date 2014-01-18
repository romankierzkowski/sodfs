package org.sodfs.storage.driver.manager.exceptions;

/**
 *
 * @author Roman Kierzkowski
 */
public class MovedReplicaException extends MovableFileException {

    public MovedReplicaException(int storageId, int fileId, int destId) {
        super("File " + fileId + " replica was moved from " + storageId + " to " + destId + ".");
    }
    
}
