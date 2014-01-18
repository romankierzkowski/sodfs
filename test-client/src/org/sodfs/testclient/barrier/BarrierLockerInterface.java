package org.sodfs.testclient.barrier;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Roman Kierzkowski
 */
public interface BarrierLockerInterface extends Remote {    
    void register() throws RemoteException;    
    void await() throws InterruptedException, RemoteException;
}
