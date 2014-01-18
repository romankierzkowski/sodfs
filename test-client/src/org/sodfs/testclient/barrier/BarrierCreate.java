package org.sodfs.testclient.barrier;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.sodfs.testclient.commons.Constants;

/**
 *
 * @author Roman Kierzkowski
 */
public class BarrierCreate {

    
    public static BarrierInterface barierRemoteObjec;
    public static BarrierInterface stub;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length == 3) {
                System.setProperty("java.rmi.server.hostname", args[0]);
                String name = args[1];                           
                int count = Integer.parseInt(args[2]);                            
                Registry rmiRegistry = getRMIRegistry(Constants.RMI_PORT);
                if (rmiRegistry != null) {
                    barierRemoteObjec = new Barrier(count);
                    stub = (BarrierInterface) UnicastRemoteObject.exportObject(barierRemoteObjec, 0);
                    rmiRegistry.rebind(Constants.BARIER_NAMESPACE + "." + name, stub);                    
                    barierRemoteObjec.getLocker().await();
                    System.exit(0);
                }
            } else {
                System.err.println("usage: sodfsbarrier <host> <barier_name> <wait_count>");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static Registry getRMIRegistry(int port) {
        Registry rmiRegistry = null;
        try {
            rmiRegistry = LocateRegistry.createRegistry(port);
        } catch (RemoteException ex) {            
        }
        try {
            rmiRegistry = LocateRegistry.getRegistry(port);
        } catch (RemoteException rex) {
            rex.printStackTrace();
        }        
        return rmiRegistry;
    }
}
