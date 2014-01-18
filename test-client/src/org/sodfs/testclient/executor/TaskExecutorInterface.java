package org.sodfs.testclient.executor;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Roman Kierzkowski
 */
public interface TaskExecutorInterface extends Remote {
    
    void execute(Task task) throws RemoteException;
    
}
