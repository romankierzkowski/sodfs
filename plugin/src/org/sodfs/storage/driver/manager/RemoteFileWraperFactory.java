package org.sodfs.storage.driver.manager;

/**
 *
 * @author Roman Kierzkowski
 */
public interface RemoteFileWraperFactory {
    MovableFileInterface wrap(MovableFileInterface replica, int localStorageId, int remoteStorageId, int fileId);    
}
