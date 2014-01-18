package org.sodfs.storage.driver.manager;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.sodfs.storage.driver.manager.exceptions.MovableFileException;

/**
 *
 * @author Roman Kierzkowski
 */
public interface MovableFileInterface extends Remote {
    
    int getStorageId() throws RemoteException, MovableFileException;
        
    void openFile(boolean createFlag) throws RemoteException, IOException, MovableFileException;

    byte[] readFile(int len, long fileOff) throws RemoteException, IOException, MovableFileException;

    int writeFile(byte[] buf, long fileOff) throws RemoteException, IOException, MovableFileException;

    long seekFile(long pos, int typ) throws RemoteException, IOException, MovableFileException; 

    void flushFile() throws RemoteException, IOException, MovableFileException;

    void truncateFile(long siz) throws RemoteException, IOException, MovableFileException;

    void closeFile() throws RemoteException, IOException, MovableFileException;
}
