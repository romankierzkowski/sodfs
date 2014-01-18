package org.sodfs.storage.driver.config;

import org.alfresco.config.ConfigElement;
import org.alfresco.jlan.server.core.DeviceContextException;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSConfigurationManager {
    
    public static final String META_SERVER_CONFIG_ELEMENT = "meta-server-address";
    public static final String META_SERVER_HOST_ATTRIBUTE = "host";
    public static final String META_SERVER_NAME_ATTRIBUTE = "name";
    public static final String META_SERVER_PORT_ATTRIBUTE = "port";
    
    public static final String STORAGE_SERVER_CONFIG_ELEMENT = "storage-server-config";
    public static final String STORAGE_SERVER_NAME_ELEMENT = "name";
    public static final String STORAGE_SERVER_HOST_ELEMENT = "host";
    public static final String STORAGE_SERVER_PORT_ELEMENT = "port";
    public static final String STORAGE_SERVER_MULTICAST_ADDRESS_ELEMENT = "multicast-address";
    public static final String STORAGE_SERVER_MULTICAST_PORT_ELEMENT = "multicast-port";
    public static final String STORAGE_SERVER_STORAGE_PATH_ELEMENT = "storage-path";
    
    public static final String SORPA_CONFIG_ELEMENT = "sorpa-config";
    public static final String SORPA_CONFIG_PC_ATTRIBUTE = "pc";
    public static final String SORPA_CONFIG_K_ATTRIBUTE = "k";
    public static final String SORPA_CONFIG_TTL_ATTRIBUTE = "ttl";
    public static final String SORPA_CONFIG_MIN_NOR_ATTRIBUTE = "min-nor";    
    public static final String SORPA_CONFIG_PIN_ATTRIBUTE = "pin";
    public static final String SORPA_CONFIG_RF_ATTRIBUTE = "rf";
    public static final String SORPA_CONFIG_DRF_ATTRIBUTE = "drf";
    public static final String SORPA_CONFIG_AF_ATTRIBUTE = "af";
    public static final String SORPA_CONFIG_MF_ATTRIBUTE = "mf";
    
    private MetaServerAddress metaServerAddress;
    private StorageServerConfig storageServerConfig;
    private SORPAConfig sorpaConfig;
    
    public SoDFSConfigurationManager(ConfigElement config) throws DeviceContextException {
        try {
            parseMetaServerAddress(config);
            parseStorageServerConfig(config);          
            parseSORPAConfig(config);            
        } catch (NumberFormatException ex) {
            throw new DeviceContextException("Inavlid configuration data: " + ex.getMessage());
        }
    }
    
    private void parseMetaServerAddress(ConfigElement config) throws NumberFormatException {
        ConfigElement ms = config.getChild(META_SERVER_CONFIG_ELEMENT);
        metaServerAddress = new MetaServerAddress();
        metaServerAddress.setName(ms.getAttribute(META_SERVER_NAME_ATTRIBUTE));
        metaServerAddress.setAddress(ms.getAttribute(META_SERVER_HOST_ATTRIBUTE));
        int metaPort = 1099;
        metaPort = Integer.parseInt(ms.getAttribute(META_SERVER_PORT_ATTRIBUTE));
        metaServerAddress.setPort(metaPort);
    }

    private void parseSORPAConfig(ConfigElement config) throws NumberFormatException {
        ConfigElement sc = config.getChild(SORPA_CONFIG_ELEMENT);
        sorpaConfig = new SORPAConfig();
        sorpaConfig.setPc(Double.parseDouble(sc.getAttribute(SORPA_CONFIG_PC_ATTRIBUTE)));
        sorpaConfig.setK(Double.parseDouble(sc.getAttribute(SORPA_CONFIG_K_ATTRIBUTE)));
        sorpaConfig.setTTL(Long.parseLong(sc.getAttribute(SORPA_CONFIG_TTL_ATTRIBUTE)));
        sorpaConfig.setPinTime(Long.parseLong(sc.getAttribute(SORPA_CONFIG_PIN_ATTRIBUTE)));
        sorpaConfig.setMinNOR(Integer.parseInt(sc.getAttribute(SORPA_CONFIG_MIN_NOR_ATTRIBUTE)));
        sorpaConfig.setRF(Float.parseFloat(sc.getAttribute(SORPA_CONFIG_RF_ATTRIBUTE)));
        sorpaConfig.setDRF(Float.parseFloat(sc.getAttribute(SORPA_CONFIG_DRF_ATTRIBUTE)));
        sorpaConfig.setAF(Float.parseFloat(sc.getAttribute(SORPA_CONFIG_AF_ATTRIBUTE)));
        sorpaConfig.setMF(Float.parseFloat(sc.getAttribute(SORPA_CONFIG_MF_ATTRIBUTE)));
    }

    private void parseStorageServerConfig(ConfigElement config) throws NumberFormatException {
        ConfigElement ss = config.getChild(STORAGE_SERVER_CONFIG_ELEMENT);
        storageServerConfig = new StorageServerConfig();
        storageServerConfig.setName(ss.getChild(STORAGE_SERVER_NAME_ELEMENT).getValue());
        storageServerConfig.setAddress(ss.getChild(STORAGE_SERVER_HOST_ELEMENT).getValue());
        storageServerConfig.setPort(Integer.parseInt(ss.getChild(STORAGE_SERVER_PORT_ELEMENT).getValue()));
        storageServerConfig.setMulticastAddress(ss.getChild(STORAGE_SERVER_MULTICAST_ADDRESS_ELEMENT).getValue());
        storageServerConfig.setMulticastPort(Integer.parseInt(ss.getChild(STORAGE_SERVER_MULTICAST_PORT_ELEMENT).getValue()));
        storageServerConfig.setStoragePath(ss.getChild(STORAGE_SERVER_STORAGE_PATH_ELEMENT).getValue());
    }

    public MetaServerAddress getMetaServerAddress() {
        return metaServerAddress;
    }

    public StorageServerConfig getStorageServerConfig() {
        return storageServerConfig;
    }

    public SORPAConfig getSorpaConfig() {
        return sorpaConfig;
    }
}
