package org.sodfs.meta.persistance;

import org.sodfs.storage.meta.api.ReplicaPKEntity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Roman Kierzkowski
 */
@Embeddable
public class ReplicaPK implements ReplicaPKEntity, Serializable {
    
    
    @Column(name = "file", nullable = false)
    private int file;
    @Column(name = "storage_server", nullable = false)
    private int storageServer;

    public ReplicaPK() {
    }

    public ReplicaPK(int file, int storageServer) {
        this.file = file;
        this.storageServer = storageServer;
    }

    public int getFile() {
        return file;
    }

    public void setFile(int file) {
        this.file = file;
    }

    public int getStorageServer() {
        return storageServer;
    }

    public void setStorageServer(int storageServer) {
        this.storageServer = storageServer;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) file;
        hash += (int) storageServer;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReplicaPK)) {
            return false;
        }
        ReplicaPK other = (ReplicaPK) object;
        if (this.file != other.file) {
            return false;
        }
        if (this.storageServer != other.storageServer) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.sodfs.meta.persistance.ReplicaPK[file=" + file + ", storageServer=" + storageServer + "]";
    }

}
