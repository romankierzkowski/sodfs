package org.sodfs.testclient.commons;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Roman Kierzkowski
 */
public class RmiRegRunner {     
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            int port = Integer.parseInt(args[0]);
            getRMIRegistry(port);        
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RmiRegRunner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else {
            System.err.println("usage: sodfsrmireg <port>");
        }
    }
    
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
