package org.sodfs.storage.driver;

import java.rmi.RemoteException;
import org.sodfs.storage.driver.manager.FileManager;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.sodfs.storage.communication.GroupCommunicator;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.communication.StorageServerRemoteObject;
import org.sodfs.storage.discovery.NetworkDiscoverer;
import org.sodfs.storage.excludes.ExcludeManager;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.replication.ReplicaPlacementManager;

/**
 *
 * @author Roman Kierzkowski
 */
public class SoDFSDeviceContext extends DiskDeviceContext{   
    private MetaDataServiceInterface metaDataService;
    private FileManager fileManager;
    private NetworkDiscoverer networkDiscoverer;
    private GroupCommunicator groupCommunicator;
    private InternodeCommunicator internodeCommunicator;
    private ReplicaPlacementManager replicaPlacementManager;
    private ExcludeManager excludeManager;
    private int storageId;

    void start() throws MetaDataServiceNotAvilableException, RemoteException {
        networkDiscoverer.start();
        excludeManager.start();
        internodeCommunicator.start();
        fileManager.start();
        replicaPlacementManager.start();        
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public MetaDataServiceInterface getMetaDataService() {
        return metaDataService;
    }

    public void setMetaDataService(MetaDataServiceInterface metaDataManager) {
        this.metaDataService = metaDataManager;
    }


    public NetworkDiscoverer getNetworkDiscoverer() {
        return networkDiscoverer;
    }

    public void setNetworkDiscoverer(NetworkDiscoverer networkDiscoverer) {
        this.networkDiscoverer = networkDiscoverer;
    }

    public GroupCommunicator getGroupCommunicator() {
        return groupCommunicator;
    }

    public void setGroupCommunicator(GroupCommunicator groupCommunicator) {
        this.groupCommunicator = groupCommunicator;
    }

    public InternodeCommunicator getInternodeCommunicator() {
        return internodeCommunicator;
    }

    public void setInternodeCommunicator(InternodeCommunicator internodeCommunicator) {
        this.internodeCommunicator = internodeCommunicator;
    }

    public ReplicaPlacementManager getReplicaPlacementManager() {
        return replicaPlacementManager;
    }

    public void setReplicaPlacementManager(ReplicaPlacementManager replicaPlacementManager) {
        this.replicaPlacementManager = replicaPlacementManager;
    }

    public ExcludeManager getExcludeManager() {
        return excludeManager;
    }

    public void setExcludeManager(ExcludeManager excludeManager) {
        this.excludeManager = excludeManager;
    }

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }
}
