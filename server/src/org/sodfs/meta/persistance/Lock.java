package org.sodfs.meta.persistance;

import org.sodfs.storage.meta.api.StorageServerEntity;
import org.sodfs.storage.meta.api.LockEntity;
import org.sodfs.storage.meta.api.FileEntity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Roman Kierzkowski
 */
@Entity
@Table(name = "locks")
public class Lock implements  LockEntity, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "lock_id", nullable = false)
    private Integer lockId;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "file", referencedColumnName = "node_id")
    private File file;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "storage_server", referencedColumnName = "storage_id")
    private StorageServer storageServer;
    @Column(name = "position", nullable = false)
    private long position;
    @Column(name = "range_length", nullable = false)
    private long rangeLength;
    @Column(name = "process_id")
    private Integer processId;

    public Lock() {
    }

    public Lock(Integer lockId) {
        this.lockId = lockId;
    }

    public Lock(Integer lockId, long position, long rangeLength) {
        this.lockId = lockId;
        this.position = position;
        this.rangeLength = rangeLength;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public long getRangeLength() {
        return rangeLength;
    }

    public void setRangeLength(long rangeLength) {
        this.rangeLength = rangeLength;
    }

    public Integer getLockId() {
        return lockId;
    }

    public void setLockId(Integer lockId) {
        this.lockId = lockId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public StorageServer getStorageServer() {
        return storageServer;
    }

    public void setStorageServer(StorageServer storageServer) {
        this.storageServer = storageServer;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lockId != null ? lockId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lock)) {
            return false;
        }
        Lock other = (Lock) object;
        if ((this.lockId == null && other.lockId != null) || (this.lockId != null && !this.lockId.equals(other.lockId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.sodfs.meta.persistance.Lock[lockId=" + lockId + "]";
    }

    public FileEntity getFileEntity() {
        return file;
    }

    public StorageServerEntity getStorageServerEntity() {
        return storageServer;
    }

}
