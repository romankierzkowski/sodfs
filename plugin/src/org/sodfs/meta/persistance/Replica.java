package org.sodfs.meta.persistance;

import org.sodfs.storage.meta.api.ReplicaPKEntity;
import org.sodfs.storage.meta.api.ReplicaEntity;
import org.sodfs.storage.meta.api.StorageServerEntity;
import org.sodfs.storage.meta.api.FileEntity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Roman Kierzkowski
 */
@Entity
@Table(name = "replicas")
@NamedQueries({@NamedQuery(name = "Replica.markMoved", 
     query = "UPDATE Replica r SET r.status = :moved WHERE r.destination.storageId  = :destinationId AND r.replicaPK.file = :fileId"),
@NamedQuery(name = "Replica.getValidReplicas", 
     query = "SELECT r FROM Replica r WHERE r.status <> :moved AND r.replicaPK.storageServer = :storageId"),
@NamedQuery(name = "Replica.getMovedReplicas", 
     query = "SELECT r FROM Replica r WHERE r.status = :moved AND r.replicaPK.storageServer = :storageId"),
@NamedQuery(name = "Replica.getFileReplicas", 
     query = "SELECT r.replicaPK.storageServer FROM Replica r WHERE (r.status = :active OR r.status = :moving) AND r.replicaPK.file = :fileId AND r.replicaPK.storageServer <> :localStorageId"),  
@NamedQuery(name = "Replica.getMovingReplicas", 
     query = "SELECT r FROM Replica r WHERE r.destination.storageId = :destination AND r.replicaPK.file = :fileId"),
@NamedQuery(name = "Replica.getFileReplicasStorageServers", 
     query = "SELECT r.storageServer FROM Replica r WHERE (r.status = :active OR r.status = :moving) AND r.replicaPK.file = :fileId")})     
public class Replica implements ReplicaEntity, Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ReplicaPK replicaPK;
    @Column(name = "status", nullable = false)
    private int status;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "file", referencedColumnName = "node_id", insertable = false, updatable = false)
    private File file;
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "storage_server", referencedColumnName = "storage_id", insertable = false, updatable = false)
    private StorageServer storageServer;
    @ManyToOne(fetch=FetchType.EAGER, optional=true)
    @JoinColumn(name = "destination", referencedColumnName = "storage_id", updatable = false)
    private StorageServer destination;

    public Replica() {
    }

    public Replica(ReplicaPK replicaPK) {
        this.replicaPK = replicaPK;
    }

    public Replica(ReplicaPK replicaPK, int status) {
        this.replicaPK = replicaPK;
        this.status = status;
    }

    public Replica(int file, int storageServer) {
        this.replicaPK = new ReplicaPK(file, storageServer);
    }

    public ReplicaPK getReplicaPK() {
        return replicaPK;
    }

    public void setReplicaPK(ReplicaPK replicaPK) {
        this.replicaPK = replicaPK;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public void setStorageServer(StorageServer storageServer1) {
        this.storageServer = storageServer1;
    }

    public FileEntity getFileEntity() {
        return file;
    }

    public ReplicaPKEntity getReplicaPKEntity() {
        return replicaPK;
    }

    public StorageServerEntity getStorageServerEntity() {
        return storageServer;
    }

    public StorageServer getDestination() {
        return destination;
    }

    public void setDestination(StorageServer destination) {
        this.destination = destination;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (replicaPK != null ? replicaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Replica)) {
            return false;
        }
        Replica other = (Replica) object;
        if ((this.replicaPK == null && other.replicaPK != null) || (this.replicaPK != null && !this.replicaPK.equals(other.replicaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.sodfs.meta.persistance.Replica[replicaPK=" + replicaPK + "]";
    }
}
