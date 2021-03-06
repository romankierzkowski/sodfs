package org.sodfs.meta.persistance;

import org.sodfs.storage.meta.api.NodeEntity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Roman Kierzkowski
 */

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
//@Table(name = "nodes", uniqueConstraints = {@UniqueConstraint(columnNames={"name","parent"})})
@Table(name = "nodes")
public abstract class Node implements Serializable, NodeEntity {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator="node_id_seq")
    @SequenceGenerator(name="node_id_seq",sequenceName="node_id_seq", initialValue=40, allocationSize=1)
    @Column(name = "node_id", nullable = false)
    private Integer nodeId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "nominal_size", nullable = false)
    private long nominalSize;
    @Column(name = "allocation_size", nullable = false)
    private long allocationSize;
    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "accessed", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date accessed;
    @Column(name = "mode", nullable = false)
    private int mode;
    @Column(name = "attribute", nullable = false)
    private int attribute;
    @Column(name = "group_id", nullable = false)
    private int groupId;
    @Column(name = "user_id", nullable = false)
    private int userId;    
    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "parent", referencedColumnName = "node_id")
    private Directory parent;

    public Node() {
    }
    
    public Node(Integer nodeId, String name, long nominalSize, long allocationSize, Date modified, Date created, Date accessed, int mode, int attribute, int groupId, int userId, Directory parent) {
        this.nodeId = nodeId;
        this.name = name;
        this.nominalSize = nominalSize;
        this.allocationSize = allocationSize;
        this.modified = modified;
        this.created = created;
        this.accessed = accessed;
        this.mode = mode;
        this.attribute = attribute;
        this.groupId = groupId;
        this.userId = userId;
        this.parent = parent;
    }
    
    public Integer getNodeId() {
        return nodeId;
    }
    
    public Date getAccessed() {
        return accessed;
    }

    public long getAllocationSize() {
        return allocationSize;
    }

    public int getAttribute() {
        return attribute;
    }

    public Date getCreated() {
        return created;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getMode() {
        return mode;
    }

    public Date getModified() {
        return modified;
    }

    public String getName() {
        return name;
    }

    public long getNominalSize() {
        return nominalSize;
    }

    public Directory getParent() {
        return parent;
    }

    public int getUserId() {
        return userId;
    }

    public void setAccessed(Date accessed) {
        this.accessed = accessed;
    }

    public void setAllocationSize(long allocationSize) {
        this.allocationSize = allocationSize;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNominalSize(long nominalSize) {
        this.nominalSize = nominalSize;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }
    
        @Override
    public int hashCode() {
        int hash = 0;
        hash += (nodeId != null ? nodeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Node)) {
            return false;
        }
        Node other = (Node) object;
        if ((this.nodeId == null && other.nodeId != null) || (this.nodeId != null && !this.nodeId.equals(other.nodeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.sodfs.meta.persistance.Node[nodeId=" + nodeId + "]";
    }
}
