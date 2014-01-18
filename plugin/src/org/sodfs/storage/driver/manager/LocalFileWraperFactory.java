package org.sodfs.storage.driver.manager;

/**
 *
 * @author Roman Kierzkowski
 */
public interface LocalFileWraperFactory {
    MovableFileInterface wrap(MovableFileInterface replica, int storageId, int fileId);    
}
