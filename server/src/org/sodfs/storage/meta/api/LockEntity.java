package org.sodfs.storage.meta.api;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public interface LockEntity extends Serializable {

    FileEntity getFileEntity();

    Integer getLockId();

    long getPosition();

    Integer getProcessId();

    long getRangeLength();

    StorageServerEntity getStorageServerEntity();

    void setPosition(long position);

    void setProcessId(Integer processId);

    void setRangeLength(long rangeLength);
}
