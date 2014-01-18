package org.sodfs.storage.meta.api;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public interface DirectoryEntity extends NodeEntity, Serializable {
    
    boolean isDirectory();

}
