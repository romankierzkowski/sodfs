package org.sodfs.storage.meta.api;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Roman Kierzkowski
 */
public interface NodeEntity extends Serializable {

    Date getAccessed();

    long getAllocationSize();

    int getAttribute();

    Date getCreated();

    int getGroupId();

    int getMode();

    Date getModified();

    String getName();

    Integer getNodeId();

    long getNominalSize();

    int getUserId();

    boolean isDirectory();

    void setAccessed(Date accessed);

    void setAllocationSize(long allocationSize);

    void setAttribute(int attribute);

    void setCreated(Date created);

    void setGroupId(int groupId);

    void setMode(int mode);

    void setModified(Date modified);

    void setName(String name);

    void setNominalSize(long nominalSize);

    void setUserId(int userId);
}
