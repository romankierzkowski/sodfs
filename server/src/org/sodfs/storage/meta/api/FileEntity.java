package org.sodfs.storage.meta.api;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Roman Kierzkowski
 */
public interface FileEntity extends NodeEntity, Serializable {

    Date getChanged();

    long getCoinTTL();

    long getPinTime();

    boolean isDirectory();

    void setChanged(Date changed);

    void setCoinTTL(long coinTTL);

    void setPinTime(long pinTime);
    
    int getMinNOR();
}
