package org.sodfs.storage.driver.manager;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;
import org.sodfs.storage.driver.manager.exceptions.NoReplicaAvilableException;
import org.sodfs.storage.driver.manager.remote.RemoteFileManager;
import org.sodfs.storage.driver.manager.local.LocalFileManager;
import org.sodfs.storage.meta.api.MetaDataServiceNotAvilableException;

/**
 *
 * @author Roman Kierzkowski
 */

public class ReplicaLocator {
    public static final int MAX_FAILURES = 3;

    private LocalFileManager localFileManager;
    private RemoteFileManager remoteFileManager;
    private LinkedList<FileWraperFactory> wraperFactories; 
    
    private static Logger logger = Logger.getLogger(ReplicaLocator.class.getName());
    
    public ReplicaLocator(LocalFileManager localFileManager, 
                          RemoteFileManager remoteFileManger,
                          LinkedList<FileWraperFactory> wraperFactories) {
        this.localFileManager = localFileManager;
        this.remoteFileManager = remoteFileManger;
        this.wraperFactories = wraperFactories; 
    }

    public MovableFileInterface getReplica(int fileId) throws NoReplicaAvilableException, MetaDataServiceNotAvilableException {
        MovableFileInterface result = null;
        try {
            result = localFileManager.getLocalReplica(fileId);            
        } catch (MovableFileException ex) {
            logger.log(Level.FINER, "Replica " + fileId + " is not avilable on local storage server.", ex);
        }
        if (result == null) {
            result = remoteFileManager.getRemoteReplica(fileId);
        }        
        if (result != null) {
            for (FileWraperFactory factory : wraperFactories) {
                try {                    
                    result = factory.wrap(result, result.getStorageId(), fileId);                    
                } catch (RemoteException ex) {
                    Logger.getLogger(ReplicaLocator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MovableFileException ex) {
                    Logger.getLogger(ReplicaLocator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }    
        }
        return result;
    }
}
