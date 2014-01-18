package org.sodfs.storage.driver.manager;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Set;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;
import org.sodfs.storage.driver.manager.remote.RemoteFileManager;
import org.sodfs.storage.driver.manager.local.LocalFileManager;
import org.sodfs.storage.driver.*;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.communication.GroupCommunicator;
import org.sodfs.storage.communication.InternodeCommunicator;
import org.sodfs.storage.discovery.NetworkDiscoverer;
import org.sodfs.storage.excludes.ExcludeManager;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;

/**
 *
 * @author Roman Kierzkowski
 */
public class FileManager {
    
    private LocalFileManager localFileManager;
    private RemoteFileManager remoteFileManager;
    
    private ReplicaLocator replicaLocator;
    
    private LinkedList<FileWraperFactory> wraperFactories = new LinkedList<FileWraperFactory>();
    
    private MetaDataServiceInterface msd; 
    private InternodeCommunicator internodeCommuniactor;
    private GroupCommunicator groupCommunicator;
    private ExcludeManager excludeManager;
    private NetworkDiscoverer networkDiscoverer;
    private long pinTime;
    
    public FileManager (int storageId, String storagePath, long pinTime) {
        localFileManager = new LocalFileManager(storageId, storagePath);
        remoteFileManager = new RemoteFileManager(storageId);
        replicaLocator = new ReplicaLocator(localFileManager, remoteFileManager, wraperFactories);
        this.pinTime = pinTime;
    }
    
    public void start() throws MetaDataServiceNotAvilableException {
        localFileManager.start();
    }

    public MovableFileInterface getLocalReplica(int fileId) throws MovableFileException {
        return localFileManager.getLocalReplica(fileId);
    }

    public void create(int fileId, int minNOR, long pinTime) {
        localFileManager.create(fileId, minNOR, pinTime);
    }

    public boolean dereplicate(int fileId) {
        return localFileManager.dereplicate(fileId);
    }

    public Set<Integer> getLocalReplicasList() {
        return localFileManager.getLocalReplicasList();
    }
    
    public boolean isOwning(int fileId) {
        return localFileManager.isOwning(fileId);
    }

    public boolean move(int fileId, int destignationStorage) {
        return localFileManager.move(fileId, destignationStorage);
    }

    public boolean replicate(int fileId, boolean move, int orginStorageId) {
        // TODO: Workaround REMOVE!!
        return localFileManager.replicate(fileId, pinTime, move, orginStorageId);
    }
    
    public SoDFSNetworkFile getNetworkFile(int fileId) {
        SoDFSNetworkFile nf = new SoDFSNetworkFile(fileId, replicaLocator);
        return nf;
    }
    
    public void addLocalFileWraperFactory(LocalFileWraperFactory factory) {
        localFileManager.addLocalFileWraperFactory(factory);
    }
    
    public void removeLocalFileWraperFactory(LocalFileWraperFactory factory) {
        localFileManager.removeLocalFileWraperFactory(factory);
    }
    
    public void addRemoteFileWraperFactory(RemoteFileWraperFactory factory) {
        remoteFileManager.addRemoteFileWraperFactory(factory);
    }
    
    public void removeLocalFileWraperFactory(RemoteFileWraperFactory factory) {
        remoteFileManager.removeLocalFileWraperFactory(factory);
    }
    
    public void addFileWraperFactory(FileWraperFactory factory) {
        wraperFactories.add(factory);
    }
    
    public void removeFileWraperFactory(FileWraperFactory factory) {
        wraperFactories.add(factory);
    }

    public void setMetaDataService(MetaDataServiceInterface msd) {
        this.msd = msd;
        localFileManager.setMetaDataService(msd);
        remoteFileManager.setMetaDataService(msd);
    }

    public void setInternodeCommuniactor(InternodeCommunicator internodeCommuniactor) {
        this.internodeCommuniactor = internodeCommuniactor;
        remoteFileManager.setInternodeCommunicator(internodeCommuniactor);
    }

    public void setGroupCommunicator(GroupCommunicator groupCommunicator) {
        this.groupCommunicator = groupCommunicator;
        localFileManager.setGroupCommunicator(groupCommunicator);
    }

    public void setExcludeManager(ExcludeManager excludeManager) {
        this.excludeManager = excludeManager;
        remoteFileManager.setExcludeManager(excludeManager);
    }

    public void setNetworkDiscoverer(NetworkDiscoverer networkDiscoverer) {
        this.networkDiscoverer = networkDiscoverer;
        remoteFileManager.setNetworkDiscoverer(networkDiscoverer);
    }

}
