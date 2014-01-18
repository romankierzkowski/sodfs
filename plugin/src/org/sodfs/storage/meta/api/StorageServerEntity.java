package org.sodfs.storage.meta.api;

import java.io.Serializable;

/**
 *
 * @author Roman Kierzkowski
 */
public interface StorageServerEntity extends Serializable{

    String getAddress();

    String getMulticastAddress();

    int getMulticastPort();

    String getName();

    int getPort();

    Integer getStorageId();

    void setAddress(String address);

    void setMulticastAddress(String multicastAddress);

    void setMulticastPort(int multicastPort);

    void setName(String name);

    void setPort(int port);
}
