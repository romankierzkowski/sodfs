package org.sodfs.storage.excludes;

import org.sodfs.utils.Clock;

/**
 *
 * @author Roman Kierzkowski
 */
public class ReplicaExclude {
    private int storageId;
    private int fileId;
       
    private boolean first = true;
    private int hash = 7;

    public ReplicaExclude(int storageId, int fileId) {
        this.storageId = storageId;
        this.fileId = fileId;        
    }
    
    @Override
    public int hashCode() {
        if (first) {
            first = false;        
            hash = 47 * hash + this.fileId;
            hash = 47 * hash + this.storageId;            
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReplicaExclude other = (ReplicaExclude) obj;
        if (this.fileId != other.fileId) {
            return false;
        }
        if (this.storageId != other.storageId) {
            return false;
        }
        return true;        
    }

    @Override
    public String toString() {
        return "(" + fileId + "," + storageId + ")";
    }    
}
