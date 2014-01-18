package org.sodfs.meta.persistance;

import org.sodfs.storage.meta.api.FileEntity;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Roman Kierzkowski
 */
@Entity
@Table(name = "files")
public class File extends Node implements FileEntity, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(name = "coin_ttl", nullable = false)
    private long coinTTL;
    @Column(name = "pin_time", nullable = false)
    private long pinTime;
    @Column(name = "min_nor", nullable = false)
    private int minNOR;
    @Column(name = "changed", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date changed;
    @OneToMany(cascade= {CascadeType.ALL}, mappedBy="file", fetch=FetchType.LAZY)
    private List<Replica> replicas;
    @OneToMany(cascade= {CascadeType.ALL}, mappedBy="file", fetch=FetchType.LAZY)
    private List<Lock> locks;

    public Date getChanged() {
        return changed;
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public List<Replica> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<Replica> replicas) {
        this.replicas = replicas;
    }

    public List<Lock> getLocks() {
        return locks;
    }

    public void setLocks(List<Lock> locks) {
        this.locks = locks;
    }

    public long getCoinTTL() {
        return coinTTL;
    }

    public void setCoinTTL(long coinTTL) {
        this.coinTTL = coinTTL;
    }

    public long getPinTime() {
        return pinTime;
    }

    public void setPinTime(long pinTime) {
        this.pinTime = pinTime;
    }

    public int getMinNOR() {
        return minNOR;
    }

    public void setMinNOR(int minNOR) {
        this.minNOR = minNOR;
    }

    @Override
    public String toString() {
        return "org.sodfs.meta.persistance.File[nodeId=" + getNodeId() + "]";
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
