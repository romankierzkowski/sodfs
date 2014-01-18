package org.sodfs.storage.driver.manager.exceptions;

/**
 *
 * @author Roman Kierzkowski
 */
public class DereplicatedReplicaException extends MovableFileException{
    
    public DereplicatedReplicaException(int storageId, int fileId) {
        super("File " + fileId + " replica was dereplicated on the storage server " + storageId + ".");
    }
    
}
