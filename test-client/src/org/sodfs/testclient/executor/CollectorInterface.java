package org.sodfs.testclient.executor;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Roman Kierzkowski
 */
public interface CollectorInterface extends Remote {
    public void readFinished() throws RemoteException;
    public void writeFinished() throws RemoteException;
}
