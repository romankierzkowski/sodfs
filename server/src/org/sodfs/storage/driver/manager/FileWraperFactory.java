package org.sodfs.storage.driver.manager;

/**
 *
 * @author Roman Kierzkowski
 */
public interface FileWraperFactory {
    MovableFileInterface wrap(MovableFileInterface replica, int storageId, int fileId);    
}
