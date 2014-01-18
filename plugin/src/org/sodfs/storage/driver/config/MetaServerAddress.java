package org.sodfs.storage.driver.config;

/**
 *
 * @author Roman Kierzkowski
 */
public class MetaServerAddress {
    private String name;
    private String address;
    private int port;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String host) {
        this.address = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
