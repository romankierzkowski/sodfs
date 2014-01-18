package org.sodfs.meta.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Roman Kierzkowski
 */
public interface MetaServerInterface extends Remote {

    public MetaDataManipulatorInterface getMetaDataManipulator() throws RemoteException;
    
    public long getTime() throws RemoteException;
    
}
