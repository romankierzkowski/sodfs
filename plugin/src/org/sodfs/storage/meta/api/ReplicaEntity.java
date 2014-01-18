package org.sodfs.storage.meta.api;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public interface ReplicaEntity extends Serializable {

    FileEntity getFileEntity();

    ReplicaPKEntity getReplicaPKEntity();

    int getStatus();

    StorageServerEntity getStorageServerEntity();

    void setStatus(int status);
    
    StorageServerEntity getDestination();

}
