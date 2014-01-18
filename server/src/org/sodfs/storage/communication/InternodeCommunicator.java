package org.sodfs.storage.communication;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sodfs.storage.driver.manager.FileManager;
import org.sodfs.storage.meta.api.MetaDataServiceInterface;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;
import org.sodfs.storage.meta.api.StorageServerEntity;
import org.sodfs.storage.replication.ReplicaPlacementManager;
import org.sodfs.utils.LRUMap;
import org.sodfs.utils.RegistryUtil;

/**
 *
 * @author Roman Kierzkowski
 */
public class InternodeCommunicator {

    private MetaDataServiceInterface mds;
    private FileManager fileManager;
    private ReplicaPlacementManager rpm;   
    
    private LRUMap<Integer,StorageServerInterface> ssiCache;
    private static final int SSI_CACHE_SIZE = 20;
    
    private Logger logger = Logger.getLogger(InternodeCommunicator.class.getName());
    
    private StorageServerRemoteObject ssro = new StorageServerRemoteObject();
    private String remoteObjectName;
    private int regPort;    

    public InternodeCommunicator(String remoteObjectName, String sotrageAddress, int regPort) {
        this.remoteObjectName = remoteObjectName;
        this.regPort = regPort;
        this.ssiCache = new LRUMap<Integer,StorageServerInterface>(SSI_CACHE_SIZE);
        System.setProperty("java.rmi.server.hostname", sotrageAddress);
    }   

    public void setReplicaPlacementManager(ReplicaPlacementManager rpm) {
        this.rpm = rpm;
        ssro.setReplicaPlacementManager(rpm);
    }
    
    public void setMetaDataService(MetaDataServiceInterface mds) {
        this.mds = mds;        
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
        ssro.setFileManager(fileManager);
    }
    
    public void start() throws RemoteException {
        Registry registry = RegistryUtil.getRMIRegistry(regPort);
        if (registry != null) {
            StorageServerInterface stub = (StorageServerInterface) UnicastRemoteObject.exportObject(ssro,0);
            registry.rebind(remoteObjectName, stub);
        }
    }
   
    public StorageServerInterface getRemoteStorageServerInterface(int storageId) throws MetaDataServiceNotAvilableException {
        StorageServerInterface result = ssiCache.get(storageId);
        if (result == null) {
            StorageServerEntity sse = mds.getStorageServerEntity(storageId);
            try {
                Registry registry = LocateRegistry.getRegistry(sse.getAddress(), sse.getPort());
                result = (StorageServerInterface) registry.lookup(sse.getName());
                if (result != null) ssiCache.put(storageId, result);
            } catch (Exception e) {
                logger.log(Level.FINE, "Unable to resolve the storage server interface for storage server: " + storageId, e);
            }     
        } 
        return result;
    }
}
