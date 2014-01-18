package org.sodfs.storage.discovery;

import java.rmi.RemoteException;
import org.sodfs.storage.communication.StorageServerInterface;
import org.sodfs.storage.driver.manager.MovableFileInterface;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;
import org.sodfs.storage.replication.Coin;
import org.sodfs.storage.replication.ReplicationOrder;
import org.sodfs.storage.replication.ReplicationRequest;

/**
 *
 * @author Roman Kierzkowski
 */
public class StorageServerInterfaceMock implements StorageServerInterface {
    private int delay;

    public StorageServerInterfaceMock(int delay) {
        this.delay = delay;
    }  
    
    public MovableFileInterface getRemoteFile(int fileId) throws MovableFileException, RemoteException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void sendCoin(Coin coin) throws RemoteException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void sendOrder(ReplicationOrder msg) throws RemoteException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void sendRequest(ReplicationRequest msg) throws RemoteException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void ping() throws RemoteException {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {            
        }
    }

}
