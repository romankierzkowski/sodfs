package org.sodfs.testclient.barrier;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Roman Kierzkowski
 */
public interface BarrierInterface extends Remote {    
    BarrierLockerInterface getLocker() throws RemoteException;    
}
