package org.sodfs.meta.persistance;

import org.sodfs.storage.meta.api.StorageServerEntity;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Roman Kierzkowski
 */
@Entity
@Table(name = "storage_servers", 
       uniqueConstraints = @UniqueConstraint(columnNames={"multicast_address", 
                                                          "multicast_port"}))
@NamedQueries({    
    @NamedQuery(name = "StorageServer.findByName", 
                query = "SELECT s FROM StorageServer s WHERE s.name = :name"),
    @NamedQuery(name = "StorageServer.findAll",
                query = "SELECT s FROM StorageServer s")
})
                           
public class StorageServer implements Serializable, StorageServerEntity {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="storage_id_seq")
    @SequenceGenerator(name="storage_id_seq",sequenceName="storage_id_seq", allocationSize=1)
    @Column(name = "storage_id", nullable = false)
    private Integer storageId;
    @Column(name = "name", unique= true, nullable = false)
    private String name;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "port", nullable = false)
    private int port;
    @Column(name = "multicast_address", nullable = false)
    private String multicastAddress;
    @Column(name = "multicast_port", nullable = false)
    private int multicastPort;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "storageServer", fetch=FetchType.LAZY)
    private Collection<Lock> lockCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "storageServer", fetch=FetchType.LAZY)
    private Collection<Replica> replicaCollection;

    public StorageServer() {
    }

    public StorageServer(Integer storageId) {
        this.storageId = storageId;
    }

    public StorageServer(Integer storageId, String name, String address, int port) {
        this.storageId = storageId;
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Collection<Lock> getLockCollection() {
        return lockCollection;
    }

    public void setLockCollection(Collection<Lock> lockCollection) {
        this.lockCollection = lockCollection;
    }

    public Collection<Replica> getReplicaCollection() {
        return replicaCollection;
    }

    public void setReplicaCollection(Collection<Replica> replicaCollection) {
        this.replicaCollection = replicaCollection;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public void setMulticastAddress(String multicastAddress) {
        this.multicastAddress = multicastAddress;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(int multicastPort) {
        this.multicastPort = multicastPort;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (storageId != null ? storageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StorageServer)) {
            return false;
        }
        StorageServer other = (StorageServer) object;
        if ((this.storageId == null && other.storageId != null) || (this.storageId != null && !this.storageId.equals(other.storageId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.sodfs.meta.persistance.StorageServer[storageId=" + storageId + "]";
    }
}
