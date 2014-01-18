package org.sodfs.utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Roman Kierzkowski
 */ 
public class RegistryUtil {
    
    public static Registry getRMIRegistry(int port) {
        Registry rmiRegistry = null;
        try {
            rmiRegistry = LocateRegistry.createRegistry(port);
        } catch (RemoteException ex) {
            try {
                rmiRegistry = LocateRegistry.getRegistry(port);
            } catch (RemoteException rex) {
                rex.printStackTrace();
            }
        }
        return rmiRegistry;
    }
    
}
