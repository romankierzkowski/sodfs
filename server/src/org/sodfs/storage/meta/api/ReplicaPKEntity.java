package org.sodfs.storage.meta.api;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public interface ReplicaPKEntity extends Serializable {

    int getFile();

    int getStorageServer();
}
