package org.sodfs.storage.driver.manager.exceptions;

/**
 *
 * @author Roman Kierzkowski
 */
public class FaultyReplicaException extends MovableFileException {

    public FaultyReplicaException(int storageId, int fileId) {
        super("File " + fileId + " replica is faulty on the storage server " + storageId + ".");        
    }
    
    public FaultyReplicaException(int storageId, int fileId, Throwable cause) {        
        super("File " + fileId + " replica is faulty on the storage server " + storageId + ".", cause);        
    }
}
