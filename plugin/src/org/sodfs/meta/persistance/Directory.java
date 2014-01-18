package org.sodfs.meta.persistance;

import org.sodfs.storage.meta.api.DirectoryEntity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Roman Kierzkowski
 */
@Entity
@Table(name = "directories")
public class Directory extends Node implements DirectoryEntity, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @OneToMany(mappedBy = "parent", cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE}, fetch=FetchType.LAZY)
    //@MapKey(name="name")
    private List<Node> childern;
    
    public List<Node> getChildren() {
        return childern;
    }

    public void setChildren(List<Node> children) {
        this.childern = children;
    }

    @Override
    public String toString() {
        return "org.sodfs.meta.persistance.Directory[nodeId=" + getNodeId() + "]";
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}
