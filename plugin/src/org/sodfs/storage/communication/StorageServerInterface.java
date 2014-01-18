package org.sodfs.storage.communication;

import org.sodfs.storage.driver.manager.MovableFileInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.sodfs.storage.driver.*;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;
import org.sodfs.storage.replication.Coin;
import org.sodfs.storage.replication.ReplicationOrder;
import org.sodfs.storage.replication.ReplicationRequest;

/**
 *
 * @author Roman Kierzkowski
 */
public interface StorageServerInterface extends Remote {
    
    public MovableFileInterface getRemoteFile(int fileId) throws MovableFileException, RemoteException;

    public void sendCoin(Coin coin) throws RemoteException;

    public void sendOrder(ReplicationOrder msg) throws RemoteException;
    
    public void sendRequest(ReplicationRequest msg) throws RemoteException;
    
    public void ping() throws RemoteException;
    
}
