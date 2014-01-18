package org.sodfs.storage.communication;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.sodfs.storage.driver.manager.FileManager;
import org.sodfs.storage.driver.manager.MovableFileInterface;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;
import org.sodfs.storage.replication.Coin;
import org.sodfs.storage.replication.ReplicaPlacementManager;
import org.sodfs.storage.replication.ReplicationOrder;
import org.sodfs.storage.replication.ReplicationRequest;

/**
 *
 * @author Roman Kierzkowski
 */
public class StorageServerRemoteObject implements StorageServerInterface{
    private FileManager fileManager;
    private ReplicaPlacementManager replicaPlacementManager;

    public StorageServerRemoteObject() {
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void setReplicaPlacementManager(ReplicaPlacementManager replicaPlacementManager) {
        this.replicaPlacementManager = replicaPlacementManager;
    }
    
    public MovableFileInterface getRemoteFile(int fileId) throws MovableFileException, RemoteException {
        MovableFileInterface mfi = fileManager.getLocalReplica(fileId);
        MovableFileInterface stub = (MovableFileInterface) UnicastRemoteObject.exportObject(mfi,0);
        return stub;
    }

    public void sendCoin(Coin coin) throws RemoteException {
        replicaPlacementManager.receive(coin);
    }

    public void sendOrder(ReplicationOrder msg) throws RemoteException {
        replicaPlacementManager.receive(msg);
    }

    public void sendRequest(ReplicationRequest msg) throws RemoteException {
        replicaPlacementManager.receive(msg);
    }

    public void ping() throws RemoteException {        
    }
}
