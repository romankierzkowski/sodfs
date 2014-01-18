package org.sodfs.storage.excludes;

/**
 *
 * @author Roman Kierzkowski
 */
public class StorageServerExclude {
    private int storageId;
    
    private boolean first = true;
    private int hash = 3;

    public StorageServerExclude(int storageId) {        
        this.storageId = storageId;
    }

    @Override
    public int hashCode() {
        if (first) {
            first = false;
            hash = 89 * hash + this.storageId;
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
        final StorageServerExclude other = (StorageServerExclude) obj;
        if (this.storageId != other.storageId) {
            return false;
        }
        return true;
    }
}
