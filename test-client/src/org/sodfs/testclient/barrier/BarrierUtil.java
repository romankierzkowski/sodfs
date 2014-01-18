package org.sodfs.testclient.barrier;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.sodfs.testclient.commons.Constants;

/**
 *
 * @author Roman Kierzkowski
 */
public class BarrierUtil {
    
    
    public static void wait(Registry registry, String name) throws InterruptedException, RemoteException, NotBoundException {
        BarrierInterface barier = (BarrierInterface) registry.lookup(Constants.BARIER_NAMESPACE + "." + name);
        BarrierLockerInterface locker = barier.getLocker();
        locker.await();
    }
    
        
    public static void register(Registry registry, String name) throws RemoteException, NotBoundException {         
        BarrierInterface barier = (BarrierInterface) registry.lookup(Constants.BARIER_NAMESPACE + "." + name);
        BarrierLockerInterface locker = barier.getLocker();
        locker.register();
    }
    
    public static Registry getRemoteRegistry(String address, int port) throws RemoteException {
        return LocateRegistry.getRegistry(address, port);
    }
}
