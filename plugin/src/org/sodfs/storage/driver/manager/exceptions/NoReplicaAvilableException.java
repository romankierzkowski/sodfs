package org.sodfs.storage.driver.manager.exceptions;

/**
 *
 * @author Roman Kierzkowski
 */
public class NoReplicaAvilableException extends MovableFileException {
    public NoReplicaAvilableException(int fileId) {
        super("No replicas for file " + fileId + " are avilable.");
    }   
}
